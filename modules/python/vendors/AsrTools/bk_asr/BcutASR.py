import json
import logging
import time
from os import PathLike
from typing import Optional

import requests

from .ASRData import ASRData, ASRDataSeg
from .BaseASR import BaseASR


__version__ = "0.0.3"

API_BASE_URL = "https://member.bilibili.com/x/bcut/rubick-interface"

# 申请上传
API_REQ_UPLOAD = API_BASE_URL + "/resource/create"

# 提交上传
API_COMMIT_UPLOAD = API_BASE_URL + "/resource/create/complete"

# 创建任务
API_CREATE_TASK = API_BASE_URL + "/task"

# 查询结果
API_QUERY_RESULT = API_BASE_URL + "/task/result"


class BcutASR(BaseASR):
    """必剪 语音识别接口"""
    headers = {
        'User-Agent': 'Bilibili/1.0.0 (https://www.bilibili.com)',
        'Content-Type': 'application/json'
    }

    def __init__(self, audio_path: [str, bytes], use_cache: bool = False):
        super().__init__(audio_path, use_cache=use_cache)
        self.session = requests.Session()
        self.task_id = None
        self.__etags = []

        self.__in_boss_key: Optional[str, None] = None
        self.__resource_id: Optional[str, None] = None
        self.__upload_id: Optional[str, None] = None
        self.__upload_urls: Optional[list[str]] = []
        self.__per_size: Optional[int, None] = None
        self.__clips: Optional[int, None] = None

        self.__etags: Optional[list[str]] = []
        self.__download_url: Optional[str, None] = None
        self.task_id: Optional[str, None] = None


    def upload(self) -> None:
        """申请上传"""
        if not self.file_binary:
            raise ValueError("none set data")
        payload = json.dumps({
            "type": 2,
            "name": "audio.mp3",
            "size": len(self.file_binary),
            "ResourceFileType": "mp3",
            "model_id": "8",
        })

        resp = requests.post(
            API_REQ_UPLOAD,
            data=payload,
            headers=self.headers
        )
        resp.raise_for_status()
        resp = resp.json()
        resp_data = resp["data"]

        self.__in_boss_key = resp_data["in_boss_key"]
        self.__resource_id = resp_data["resource_id"]
        self.__upload_id = resp_data["upload_id"]
        self.__upload_urls = resp_data["upload_urls"]
        self.__per_size = resp_data["per_size"]
        self.__clips = len(resp_data["upload_urls"])

        logging.info(
            f"申请上传成功, 总计大小{resp_data['size'] // 1024}KB, {self.__clips}分片, 分片大小{resp_data['per_size'] // 1024}KB: {self.__in_boss_key}"
        )
        self.__upload_part()
        self.__commit_upload()

    def __upload_part(self) -> None:
        """上传音频数据"""
        for clip in range(self.__clips):
            start_range = clip * self.__per_size
            end_range = (clip + 1) * self.__per_size
            logging.info(f"开始上传分片{clip}: {start_range}-{end_range}")
            resp = requests.put(
                self.__upload_urls[clip],
                data=self.file_binary[start_range:end_range],
                headers=self.headers
            )
            resp.raise_for_status()
            etag = resp.headers.get("Etag")
            self.__etags.append(etag)
            logging.info(f"分片{clip}上传成功: {etag}")

    def __commit_upload(self) -> None:
        """提交上传数据"""
        data = json.dumps({
            "InBossKey": self.__in_boss_key,
            "ResourceId": self.__resource_id,
            "Etags": ",".join(self.__etags),
            "UploadId": self.__upload_id,
            "model_id": "8",
        })
        resp = requests.post(
            API_COMMIT_UPLOAD,
            data=data,
            headers=self.headers
        )
        resp.raise_for_status()
        resp = resp.json()
        self.__download_url = resp["data"]["download_url"]
        logging.info(f"提交成功")

    def create_task(self) -> str:
        """开始创建转换任务"""
        resp = requests.post(
            API_CREATE_TASK, json={"resource": self.__download_url, "model_id": "8"}, headers=self.headers
        )
        resp.raise_for_status()
        resp = resp.json()
        self.task_id = resp["data"]["task_id"]
        logging.info(f"任务已创建: {self.task_id}")
        return self.task_id

    def result(self, task_id: Optional[str] = None):
        """查询转换结果"""
        resp = requests.get(API_QUERY_RESULT, params={"model_id": 7, "task_id": task_id or self.task_id}, headers=self.headers)
        resp.raise_for_status()
        resp = resp.json()
        return resp["data"]

    def _run(self):
        self.upload()
        self.create_task()
        # 轮询检查任务状态
        for _ in range(500):
            task_resp = self.result()
            if task_resp["state"] == 4:
                break
            time.sleep(1)
        logging.info(f"转换成功")
        return json.loads(task_resp["result"])

    def _make_segments(self, resp_data: dict) -> list[ASRDataSeg]:
        return [ASRDataSeg(u['transcript'], u['start_time'], u['end_time']) for u in resp_data['utterances']]


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s")
    # Example usage
    audio_file = r"test.mp3"
    asr = BcutASR(audio_file)
    asr_data = asr.run()
    print(asr_data)
