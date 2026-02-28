package com.bytedesk.video.janus.client.plugins.videoroom.models;

import com.alibaba.fastjson2.JSONObject;

/**
 * Represents a video room with its configuration properties.
 *
 * @param room                 The unique numeric ID of the room.
 * @param description          The pretty name of the room.
 * @param pinRequired          Whether a PIN is required to join this room.
 * @param isPrivate            Whether this room is 'private' (hidden from lists).
 * @param maxPublishers        The maximum number of concurrent publishers.
 * @param bitrate              The bitrate cap for publishers.
 * @param bitrateCap           Whether the bitrate cap is a hard limit.
 * @param firFreq              The frequency of FIR/PLI requests to publishers.
 * @param requirePvtid         Whether subscribers must provide a private_id.
 * @param requireE2ee          Whether end-to-end encryption is required.
 * @param dummyPublisher       Whether a dummy publisher exists for placeholder subscriptions.
 * @param notifyJoining        Whether to notify all participants when a new one joins.
 * @param audioCodec           The comma-separated list of allowed audio codecs.
 * @param videoCodec           The comma-separated list of allowed video codecs.
 * @param opusFec              Whether inband FEC is enabled for Opus.
 * @param opusDtx              Whether DTX is enabled for Opus.
 * @param record               Whether the room is being recorded.
 * @param recDir               The directory for recordings.
 * @param lockRecord           Whether recording can only be controlled via secret.
 * @param numParticipants      The current number of participants.
 * @param audioLevelExt        Whether the ssrc-audio-level RTP extension is used.
 * @param audioLevelEvent      Whether to emit audio level events.
 * @param audioActivePackets   The number of packets for audio level calculation.
 * @param audioLevelAverage    The average audio level threshold.
 * @param videoOrientExt       Whether the video-orientation RTP extension is used.
 * @param playoutDelayExt      Whether the playout-delay RTP extension is used.
 * @param transportWideCcExt   Whether the transport-wide-cc RTP extension is used.
 */
public record VideoRoom(
    long room,
    String description,
    boolean pinRequired,
    boolean isPrivate,
    int maxPublishers,
    int bitrate,
    boolean bitrateCap,
    int firFreq,
    boolean requirePvtid,
    boolean requireE2ee,
    boolean dummyPublisher,
    boolean notifyJoining,
    String audioCodec,
    String videoCodec,
    boolean opusFec,
    boolean opusDtx,
    boolean record,
    String recDir,
    boolean lockRecord,
    int numParticipants,
    boolean audioLevelExt,
    boolean audioLevelEvent,
    int audioActivePackets,
    int audioLevelAverage,
    boolean videoOrientExt,
    boolean playoutDelayExt,
    boolean transportWideCcExt) {

    /**
     * Creates a {@link VideoRoom} instance from a {@link JSONObject}.
     *
     * @param json The JSON object representing the video room.
     * @return A new {@link VideoRoom} instance, or null if the input is null.
     */
    public static VideoRoom fromJson(JSONObject json) {
        if (json == null) {
            return null;
        }
        return new VideoRoom(
            json.getLongValue("room"),
            json.getString("description") == null ? "" : json.getString("description"),
            json.getBooleanValue("pin_required"),
            json.getBooleanValue("is_private"),
            json.getIntValue("max_publishers"),
            json.getIntValue("bitrate"),
            json.getBooleanValue("bitrate_cap"),
            json.getIntValue("fir_freq"),
            json.getBooleanValue("require_pvtid"),
            json.getBooleanValue("require_e2ee"),
            json.getBooleanValue("dummy_publisher"),
            json.getBooleanValue("notify_joining"),
            json.getString("audiocodec") == null ? "" : json.getString("audiocodec"),
            json.getString("videocodec") == null ? "" : json.getString("videocodec"),
            json.containsKey("opus_fec") ? json.getBooleanValue("opus_fec") : true,
            json.getBooleanValue("opus_dtx"),
            json.getBooleanValue("record"),
            json.getString("rec_dir") == null ? "" : json.getString("rec_dir"),
            json.getBooleanValue("lock_record"),
            json.containsKey("num_participants") ? json.getIntValue("num_participants") : -1,
            json.containsKey("audiolevel_ext") ? json.getBooleanValue("audiolevel_ext") : true,
            json.getBooleanValue("audiolevel_event"),
            json.containsKey("audio_active_packets") ? json.getIntValue("audio_active_packets") : 100,
            json.containsKey("audio_level_average") ? json.getIntValue("audio_level_average") : 25,
            json.containsKey("videoorient_ext") ? json.getBooleanValue("videoorient_ext") : true,
            json.containsKey("playoutdelay_ext") ? json.getBooleanValue("playoutdelay_ext") : true,
            json.containsKey("transport_wide_cc_ext") ? json.getBooleanValue("transport_wide_cc_ext") : true
        );
    }

    public static VideoRoom fromJson(org.json.JSONObject json) {
        if (json == null) {
            return null;
        }
        return fromJson(JSONObject.parseObject(json.toString()));
    }
}
