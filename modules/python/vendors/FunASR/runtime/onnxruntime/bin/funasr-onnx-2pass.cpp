/**
 * Copyright FunASR (https://github.com/alibaba-damo-academy/FunASR). All Rights Reserved.
 * MIT License  (https://opensource.org/licenses/MIT)
*/

#ifndef _WIN32
#include <sys/time.h>
#else
#include <win_func.h>
#endif

#include <iostream>
#include <fstream>
#include <sstream>
#include <map>
#include <glog/logging.h>
#include "util.h"
#include "funasrruntime.h"
#include "tclap/CmdLine.h"
#include "com-define.h"
#include "audio.h"

using namespace std;

bool is_target_file(const std::string& filename, const std::string target) {
    std::size_t pos = filename.find_last_of(".");
    if (pos == std::string::npos) {
        return false;
    }
    std::string extension = filename.substr(pos + 1);
    return (extension == target);
}

void GetValue(TCLAP::ValueArg<std::string>& value_arg, string key, std::map<std::string, std::string>& model_path)
{
    model_path.insert({key, value_arg.getValue()});
    LOG(INFO)<< key << " : " << value_arg.getValue();
}

int main(int argc, char** argv)
{
    google::InitGoogleLogging(argv[0]);
    FLAGS_logtostderr = true;

    TCLAP::CmdLine cmd("funasr-onnx-2pass", ' ', "1.0");
    TCLAP::ValueArg<std::string>    offline_model_dir("", OFFLINE_MODEL_DIR, "the asr offline model path, which contains model.onnx, config.yaml, am.mvn", true, "", "string");
    TCLAP::ValueArg<std::string>    online_model_dir("", ONLINE_MODEL_DIR, "the asr online model path, which contains model.onnx, decoder.onnx, config.yaml, am.mvn", true, "", "string");
    TCLAP::ValueArg<std::string>    quantize("", QUANTIZE, "true (Default), load the model of model.onnx in model_dir. If set true, load the model of model_quant.onnx in model_dir", false, "true", "string");
    TCLAP::ValueArg<std::string>    vad_dir("", VAD_DIR, "the vad online model path, which contains model.onnx, vad.yaml, vad.mvn", false, "", "string");
    TCLAP::ValueArg<std::string>    vad_quant("", VAD_QUANT, "true (Default), load the model of model.onnx in vad_dir. If set true, load the model of model_quant.onnx in vad_dir", false, "true", "string");
    TCLAP::ValueArg<std::string>    punc_dir("", PUNC_DIR, "the punc online model path, which contains model.onnx, punc.yaml", false, "", "string");
    TCLAP::ValueArg<std::string>    punc_quant("", PUNC_QUANT, "true (Default), load the model of model.onnx in punc_dir. If set true, load the model of model_quant.onnx in punc_dir", false, "true", "string");
    TCLAP::ValueArg<std::string>    itn_dir("", ITN_DIR, "the itn model(fst) path, which contains zh_itn_tagger.fst and zh_itn_verbalizer.fst", false, "", "string");
    TCLAP::ValueArg<std::string>    lm_dir("", LM_DIR, "the lm model path, which contains compiled models: TLG.fst, config.yaml, lexicon.txt ", false, "", "string");
    TCLAP::ValueArg<float>    global_beam("", GLOB_BEAM, "the decoding beam for beam searching ", false, 3.0, "float");
    TCLAP::ValueArg<float>    lattice_beam("", LAT_BEAM, "the lattice generation beam for beam searching ", false, 3.0, "float");
    TCLAP::ValueArg<float>    am_scale("", AM_SCALE, "the acoustic scale for beam searching ", false, 10.0, "float");
    TCLAP::ValueArg<std::int32_t>   fst_inc_wts("", FST_INC_WTS, "the fst hotwords incremental bias", false, 20, "int32_t");
    TCLAP::ValueArg<std::string>    asr_mode("", ASR_MODE, "offline, online, 2pass", false, "2pass", "string");
    TCLAP::ValueArg<std::int32_t>   onnx_thread("", "model-thread-num", "onnxruntime SetIntraOpNumThreads", false, 1, "int32_t");

    TCLAP::ValueArg<std::string> wav_path("", WAV_PATH, "the input could be: wav_path, e.g.: asr_example.wav; pcm_path, e.g.: asr_example.pcm; wav.scp, kaldi style wav list (wav_id \t wav_path)", true, "", "string");
    TCLAP::ValueArg<std::int32_t>   audio_fs("", AUDIO_FS, "the sample rate of audio", false, 16000, "int32_t");
    TCLAP::ValueArg<std::string>    hotword("", HOTWORD, "the hotword file, one hotword perline, Format: Hotword Weight (could be: 阿里巴巴 20)", false, "", "string");

    cmd.add(offline_model_dir);
    cmd.add(online_model_dir);
    cmd.add(quantize);
    cmd.add(vad_dir);
    cmd.add(vad_quant);
    cmd.add(punc_dir);
    cmd.add(punc_quant);
    cmd.add(lm_dir);
    cmd.add(global_beam);
    cmd.add(lattice_beam);
    cmd.add(am_scale);
    cmd.add(fst_inc_wts);
    cmd.add(itn_dir);
    cmd.add(wav_path);
    cmd.add(audio_fs);
    cmd.add(asr_mode);
    cmd.add(onnx_thread);
    cmd.add(hotword);
    cmd.parse(argc, argv);

    std::map<std::string, std::string> model_path;
    GetValue(offline_model_dir, OFFLINE_MODEL_DIR, model_path);
    GetValue(online_model_dir, ONLINE_MODEL_DIR, model_path);
    GetValue(quantize, QUANTIZE, model_path);
    GetValue(vad_dir, VAD_DIR, model_path);
    GetValue(vad_quant, VAD_QUANT, model_path);
    GetValue(punc_dir, PUNC_DIR, model_path);
    GetValue(punc_quant, PUNC_QUANT, model_path);
    GetValue(lm_dir, LM_DIR, model_path);
    GetValue(itn_dir, ITN_DIR, model_path);
    GetValue(wav_path, WAV_PATH, model_path);
    GetValue(asr_mode, ASR_MODE, model_path);

    struct timeval start, end;
    gettimeofday(&start, nullptr);
    int thread_num = onnx_thread.getValue();
    int asr_mode_ = -1;
    if(model_path[ASR_MODE] == "offline"){
        asr_mode_ = 0;
    }else if(model_path[ASR_MODE] == "online"){
        asr_mode_ = 1;
    }else if(model_path[ASR_MODE] == "2pass"){
        asr_mode_ = 2;
    }else{
        LOG(ERROR) << "Wrong asr-mode : " << model_path[ASR_MODE];
        exit(-1);
    }
    FUNASR_HANDLE tpass_handle=FunTpassInit(model_path, thread_num);

    if (!tpass_handle)
    {
        LOG(ERROR) << "FunTpassInit init failed";
        exit(-1);
    }
    float glob_beam = 3.0f;
    float lat_beam = 3.0f;
    float am_sc = 10.0f;
    if (lm_dir.isSet()) {
        glob_beam = global_beam.getValue();
        lat_beam = lattice_beam.getValue();
        am_sc = am_scale.getValue();
    }
    // init wfst decoder
    FUNASR_DEC_HANDLE decoder_handle = FunASRWfstDecoderInit(tpass_handle, ASR_TWO_PASS, glob_beam, lat_beam, am_sc);

    gettimeofday(&end, nullptr);
    long seconds = (end.tv_sec - start.tv_sec);
    long modle_init_micros = ((seconds * 1000000) + end.tv_usec) - (start.tv_usec);
    LOG(INFO) << "Model initialization takes " << (double)modle_init_micros / 1000000 << " s";

    // hotword file
    unordered_map<string, int> hws_map;
    std::string nn_hotwords_ = "";
    std::string hotword_path = hotword.getValue();
    LOG(INFO) << "hotword path: " << hotword_path;
    funasr::ExtractHws(hotword_path, hws_map, nn_hotwords_);

    // read wav_path
    vector<string> wav_list;
    vector<string> wav_ids;
    string default_id = "wav_default_id";
    string wav_path_ = model_path.at(WAV_PATH);

    if(is_target_file(wav_path_, "scp")){
        ifstream in(wav_path_);
        if (!in.is_open()) {
            LOG(ERROR) << "Failed to open file: " << model_path.at(WAV_SCP) ;
            return 0;
        }
        string line;
        while(getline(in, line))
        {
            istringstream iss(line);
            string column1, column2;
            iss >> column1 >> column2;
            wav_list.emplace_back(column2);
            wav_ids.emplace_back(column1);
        }
        in.close();
    }else{
        wav_list.emplace_back(wav_path_);
        wav_ids.emplace_back(default_id);
    }

    // load hotwords list and build graph
    FunWfstDecoderLoadHwsRes(decoder_handle, fst_inc_wts.getValue(), hws_map);

    std::vector<std::vector<float>> hotwords_embedding = CompileHotwordEmbedding(tpass_handle, nn_hotwords_, ASR_TWO_PASS);
    // init online features
    std::vector<int> chunk_size = {5,10,5};
    FUNASR_HANDLE tpass_online_handle=FunTpassOnlineInit(tpass_handle, chunk_size);
    float snippet_time = 0.0f;
    long taking_micros = 0;
    for (int i = 0; i < wav_list.size(); i++) {
        auto& wav_file = wav_list[i];
        auto& wav_id = wav_ids[i];

        int32_t sampling_rate_ = audio_fs.getValue();
        funasr::Audio audio(1);
		if(is_target_file(wav_file.c_str(), "wav")){
			if(!audio.LoadWav2Char(wav_file.c_str(), &sampling_rate_)){
				LOG(ERROR)<<"Failed to load "<< wav_file;
                exit(-1);
            }
		}else if(is_target_file(wav_file.c_str(), "pcm")){
			if (!audio.LoadPcmwav2Char(wav_file.c_str(), &sampling_rate_)){
				LOG(ERROR)<<"Failed to load "<< wav_file;
                exit(-1);
            }
		}else{
			if (!audio.FfmpegLoad(wav_file.c_str(), true)){
				LOG(ERROR)<<"Failed to load "<< wav_file;
                exit(-1);
            }
		}
        char* speech_buff = audio.GetSpeechChar();
        int buff_len = audio.GetSpeechLen()*2;

        int step = 800*2;
        bool is_final = false;
        string online_res="";
        string tpass_res="";
        string time_stamp_res="";
        std::vector<std::vector<string>> punc_cache(2);
        for (int sample_offset = 0; sample_offset < buff_len; sample_offset += std::min(step, buff_len - sample_offset)) {
            if (sample_offset + step >= buff_len - 1) {
                    step = buff_len - sample_offset;
                    is_final = true;
                } else {
                    is_final = false;
            }
            gettimeofday(&start, nullptr);
            FUNASR_RESULT result = FunTpassInferBuffer(tpass_handle, tpass_online_handle, 
                speech_buff+sample_offset, step, punc_cache, is_final, sampling_rate_, "pcm", 
                (ASR_TYPE)asr_mode_, hotwords_embedding, true, decoder_handle);
            gettimeofday(&end, nullptr);
            seconds = (end.tv_sec - start.tv_sec);
            taking_micros += ((seconds * 1000000) + end.tv_usec) - (start.tv_usec);

            if (result)
            {
                string online_msg = FunASRGetResult(result, 0);
                online_res += online_msg;
                if(online_msg != ""){
                    LOG(INFO)<< wav_id <<" : "<<online_msg;
                }
                string tpass_msg = FunASRGetTpassResult(result, 0);
                tpass_res += tpass_msg;
                if(tpass_msg != ""){
                    LOG(INFO)<< wav_id <<" offline results : "<<tpass_msg;
                }
                string stamp = FunASRGetStamp(result);
                if(stamp !=""){
                    LOG(INFO) << wav_ids[i] << " time stamp : " << stamp;
                    if(time_stamp_res == ""){
                        time_stamp_res += stamp;
                    }else{
                        time_stamp_res = time_stamp_res.erase(time_stamp_res.length()-1)
                                         + "," + stamp.substr(1);
                    }
                }
                snippet_time += FunASRGetRetSnippetTime(result);
                FunASRFreeResult(result);
            }
        }
        if(asr_mode_==2){
            LOG(INFO) << wav_id << " Final online  results "<<" : "<<online_res;
        }
        if(asr_mode_==1){
            LOG(INFO) << wav_id << " Final online  results "<<" : "<<tpass_res;
        }
        if(asr_mode_==0 || asr_mode_==2){
            LOG(INFO) << wav_id << " Final offline results " <<" : "<<tpass_res;
            if(time_stamp_res!=""){
                LOG(INFO) << wav_id << " Final timestamp results " <<" : "<<time_stamp_res;
            }
        }
    }

    FunWfstDecoderUnloadHwsRes(decoder_handle);
    LOG(INFO) << "Audio length: " << (double)snippet_time << " s";
    LOG(INFO) << "Model inference takes: " << (double)taking_micros / 1000000 <<" s";
    LOG(INFO) << "Model inference RTF: " << (double)taking_micros/ (snippet_time*1000000);
    FunASRWfstDecoderUninit(decoder_handle);
    FunTpassOnlineUninit(tpass_online_handle);
    FunTpassUninit(tpass_handle);
    return 0;
}

