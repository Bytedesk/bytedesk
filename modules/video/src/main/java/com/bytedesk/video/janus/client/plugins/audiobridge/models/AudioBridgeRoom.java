package com.bytedesk.video.janus.client.plugins.audiobridge.models;

import com.alibaba.fastjson2.JSONObject;

/**
 * Represents an AudioBridge room, providing details about a specific conference room.
 * This record is immutable and thread-safe.
 *
 * @param room              The unique numeric ID of the room.
 * @param description       A friendly name or description for the room.
 * @param isPrivate         Whether the room is private and hidden from public lists.
 * @param pinRequired       Indicates if a PIN is required to join this room.
 * @param samplingRate      The audio sampling rate of the mixer (e.g., 16000 for wideband).
 * @param spatialAudio      Whether the mix supports spatial (stereo) audio to position participants.
 * @param record            Indicates if the room's mixed audio is being recorded.
 * @param numParticipants   The current number of participants in the room.
 * @param permanent         Whether the room is saved in the config file
 */
public record AudioBridgeRoom(
    long room,
    String description,
    boolean isPrivate,
    boolean pinRequired,
    int samplingRate,
    boolean spatialAudio,
    boolean record,
    int numParticipants,
    boolean permanent) {

    /**
     * Constructs an {@link AudioBridgeRoom} from a {@link JSONObject}.
     * This factory method simplifies the creation of room objects from JSON responses.
     *
     * @param json The {@link JSONObject} containing the room details.
     * @return A new instance of {@link AudioBridgeRoom}.
     */
    public static AudioBridgeRoom fromJson(JSONObject json) {
        return new AudioBridgeRoom(
            json.getLongValue("room"),
            json.getString("description") == null ? "" : json.getString("description"),
            json.getBooleanValue("is_private"),
            json.getBooleanValue("pin_required"),
            json.getIntValue("sampling_rate"),
            json.getBooleanValue("spatial_audio"),
            json.getBooleanValue("record"),
            json.getIntValue("num_participants"),
            json.getBooleanValue("permanent")
        );
    }

    public static AudioBridgeRoom fromJson(org.json.JSONObject json) {
        if (json == null) {
            return null;
        }
        return fromJson(JSONObject.parseObject(json.toString()));
    }

    public JSONObject toJsonObject() {
        return new JSONObject()
            .fluentPut("room", room)
            .fluentPut("description", description)
            .fluentPut("is_private", isPrivate)
            .fluentPut("pin_required", pinRequired)
            .fluentPut("sampling_rate", samplingRate)
            .fluentPut("spatial_audio", spatialAudio)
            .fluentPut("record", record)
            .fluentPut("num_participants", numParticipants)
            .fluentPut("permanent", permanent);
    }
}
