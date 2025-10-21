package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

/**
 * 参考： https://freeswitch.org/confluence/display/FREESWITCH/mod_dptools
 * Dialplan tools provide the apps (commands) to process call sessions in XML dialplans.
 *
 * @author th158
 */
@Getter
public enum AppEnum {

    ANSWER("answer", "Answer the call for a channel."),
    ATT_XFER("att_xfer", "Attended Transfer."),


    BGSYSTEM("bgsystem", "Execute an operating system command in the background."),
    BIND_DIGIT_ACTION("bind_digit_action", "Bind a key sequence or regex to an action."),
    BIND_META_APP("bind_meta_app", "Respond to certain DTMF sequences on specified call leg(s) during a bridge and execute another dialplan application."),
    BLOCK_DTMF("block_dtmf", "Block DTMFs from being sent or received on the channel."),

    BREAK("break", "Cancel an application currently running on the channel."),
    BRIDGE("bridge", "Bridge a new channel to the existing one."),
    BRIDGE_EXPORT("bridge_export", "Export a channel variable across any bridge."),

    CALLCENTER("callcenter", "Inbound caller join a callcenter queue"),
    CAPTURE("capture", "Capture data into a channel variable."),
    CHAT("chat", "Send a text message to an IM client"),
    CHECK_ACL("check_acl", "Check originating address against an Access Control List"),
    CLEAR_DIGIT_ACTION("clear_digit_action", "Clear all digit bindings"),
    CLEAR_SPEECH_CACHE("clear_speech_cache", "Clear speech handle cache."),
    CLUECHOO("cluechoo", "Console-only \"ConCon\" choo-choo train"),
    CNG_PLC("cng_plc", "Packet Loss Concealment on lost packets + comfort noise generation"),
    CONFERENCE("conference", "Establish an inbound or outbound conference call"),

    DB("db", "insert information into the database."),
    DEFLECT("deflect", "Send a call deflect/refer."),
    DELAY_ECHO("delay_echo", "Echo audio at a specified delay."),
    DETECT_SPEECH("detect_speech", "Implements speech recognition."),
    DIGIT_ACTION_SET_REALM("digit_action_set_realm", "Change binding realm."),
    DISPLACE_SESSION("displace_session", "Displace audio on a channel."),

    EARLY_HANGUP("early_hangup", "Enable early hangup on a channel."),
    EAVESDROP("eavesdrop", "Spy on a channel."),
    ECHO("echo", "Echo audio and video back to the originator."),
    ENABLE_HEARTBEAT("enable_heartbeat", "Enable Media Heartbeat."),
    ENDLESS_PLAYBACK("endless_playback", "Continuously play file to caller.[old wiki]"),

    ENUM("enum",  "Perform E.164 lookup."),
    ERLANG("erlang", "Handle a call using Erlang."),
    EVAL("eval", "Evaluates a string."),
    EVENT("event", "Fire an event."),
    EXECUTE_EXTENSION("execute_extension", "Execute an extension from within another extension and return."),
    EXPORT("export", "Export a channel variable across a bridge <varname>=<value>"),

    FAX_DETECT("fax_detect", "Detect FAX CNG - may be deprecated."),
    FIFO("fifo", "Send caller to a FIFO queue."),
    FIFO_TRACK_CALL("fifo_track_call", "Count a call as a FIFO call in the manual_calls queue."),
    FLUSH_DTMF("flush_dtmf", "Flush any queued DTMF."),

    GENTONES("gentones", "Generate TGML tones."),
    GROUP("group", "Insert or delete members in a group."),

    HANGUP("hangup", "Hang up the current channel."),
    HASH("hash", "Add a hash to the db."),
    HOLD("hold", "Send a hold message."),
    HTTAPI("httapi", "Send call control to a Web server with the HTTAPI infrastructure"),

    INFO("info", "Display Call Info."),
    INTERCEPT("intercept", "Lets you pickup a call and take it over if you know the uuid."),
    IVR("ivr", "Run an IVR menu.[old wiki]"),

    JAVASCRIPT("javascript", "Run a JavaScript script from the dialplan"),
    JITTERBUFFER("jitterbuffer", "Send a jitter buffer message to a session"),

    LIMIT("limit", "Set a limit on number of calls to/from a resource"),
    LIMIT_EXECUTE("limit_execute", "Set the limit on a specific application"),
    LIMIT_HASH("limit_hash", "Set a limit on number of calls to/from a resource"),
    LIMIT_HASH_EXECUTE("limit_hash_execute", "Set the limit on a specific application"),
    LOG("log", "Logs a channel variable for the channel calling the application"),
    LOOP_PLAYBACK("loop_playback", "Playback a file to the channel looply for limted times"),
    LUA("lua", "Run a Lua script from the dialplan [API link]"),

    MEDIA_RESET("media_reset", "Reset all bypass/proxy media flags."),
    MKDIR("mkdir", "Create a directory."),
    MULTISET("multiset", "Set multiple channel variables with a single action."),
    MUTEX("mutex", "Block on a call flow, allowing only one at a time"),

    PAGE("page", "Play an audio file as a page."),
    PARK("park", "Park a call."),
    PARK_STATE("park_state", "Park State."),
    PHRASE("phrase", "Say a Phrase."),
    PICKUP("pickup", "Pickup a call."),
    PLAY_AND_DETECT_SPEECH("play_and_detect_speech", "Play while doing speech recognition."),
    PLAY_AND_GET_DIGITS("play_and_get_digits", "Play and get Digits."),
    PLAY_FSV("play_fsv", "Play an FSV file. FSV - (FS Video File Format) additional description needed"),
    PLAYBACK("playback", "Play a sound file to the originator."),
    PRE_ANSWER("pre_answer", "Answer a channel in early media mode.[old wiki]"),
    PREPROCESS("preprocess", "description needed"),
    PRESENCE("presence", "Send Presence"),
    PRIVACY("privacy", "Set caller privacy on calls."),

    QUEUE_DTMF("queue_dtmf", "Send DTMF digits after a successful bridge."),

    READ("read", "Read Digits."),
    RECORD("record", "Record a file from the channel's input."),
    RECORD_FSV("record_fsv", "Record a FSV file. FSV - (FS Video File Format) additional description needed"),
    RECORD_SESSION("record_session", "Record Session."),
    RECOVERY_REFRESH("recovery_refresh", "Send a recovery refresh. addition information needed"),
    REDIRECT("redirect", "Send a redirect message to a session."),
    REGEX("regex", "Perform a regex."),
    REMOVE_BUGS("remove_bugs", "Remove media bugs."),
    RENAME("rename", "Rename file."),
    RESPOND("respond", "Send a respond message to a session."),
    RING_READY("ring_ready", "Indicate Ring_Ready on a channel."),
    RXFAX("rxfax", "Receive a fax as a tif file."),

    SAY("say", "Say time/date/ip_address/digits/etc. With pre-recorded prompts."),
    SCHED_BROADCAST("sched_broadcast", "Enable Scheduled Broadcast."),
    SCHED_CANCEL("sched_cancel", "Cancel a scheduled future broadcast/transfer."),
    SCHED_HANGUP("sched_hangup", "Enable Scheduled Hangup."),
    SCHED_HEARTBEAT("sched_heartbeat", "Enable Scheduled Heartbeat. Additional information needed"),
    SCHED_TRANSFER("sched_transfer", "Enable Scheduled Transfer."),
    SEND_DISPLAY("send_display", "Sends an info packet with a sipfrag."),
    SEND_DTMF("send_dtmf", "Send inband DTMF, 2833, or SIP Info digits from a session."),
    SEND_INFO("send_info", "Send info to the endpoint."),
    SESSION_LOGLEVEL("session_loglevel", "Override the system's loglevel for this channel."),
    SET("set", "Set a channel variable for the channel calling the application."),
    SET_AUDIO_LEVEL("set_audio_level", "Adjust the read or write audio levels for a channel."),
    SET_GLOBAL("set_global", "Set a global variable."),
    SET_NAME("set_name", "Name the channel."),
    SET_PROFILE_VAR("set_profile_var", "Set a caller profile variable."),
    SET_USER("set_user", "Set a user."),
    SET_ZOMBIE_EXEC("set_zombie_exec", "Sets the zombie execution flag on the current channel."),
    SLEEP("sleep", "Pause a channel."),
    SOCKET("socket", "Establish an outbound socket connection."),
    SOUND_TEST("sound_test", "Analyze Audio."),
    SPEAK("speak", "Speaks a string or file of text to the channel using the defined TTS engine.[old wiki]"),
    SOFT_HOLD("soft_hold", "Put a bridged channel on hold."),
    START_DTMF("start_dtmf", "Start inband DTMF detection."),
    STOP_DTMF("stop_dtmf", "Stop inband DTMF detection."),
    START_DTMF_GENERATE("start_dtmf_generate", "Start inband DTMF generation."),
    STOP_DISPLACE_SESSION("stop_displace_session", "Stop displacement audio on a channel."),
    STOP_DTMF_GENERATE("stop_dtmf_generate", "Stop inband DTMF generation."),
    STOP_RECORD_SESSION("stop_record_session", "Stop Record Session."),
    STOP_TONE_DETECT("stop_tone_detect", "Stop detecting tones."),
    STRFTIME("strftime", "Returns formatted date and time."),
    SYSTEM("system", "Execute an operating system command."),

    THREE_WAY("three_way", "Three way call with a UUID."),
    TONE_DETECT("tone_detect", "Detect the presence of a tone and execute a command if found."),
    TRANSFER("transfer", "Immediately transfer the calling channel to a new extension.[old wiki]"),
    TRANSLATE("translate", "Number translation."),

    UNBIND_META_APP("unbind_meta_app", "Unbind a key from an application."),
    UNSET("unset", "Unset a variable."),
    UNHOLD("unhold", "Send a un-hold message."),
    USERSPY("userspy", "Provides persistent eavesdrop on all channels bridged to a certain user using eavesdrop."),

    VERBOSE_EVENTS("verbose_events", "Make ALL Events verbose (Make all variables appear in every single event for this channel)."),
    WAIT_FOR_SILENCE("wait_for_silence", "Pause processing while waiting for silence on the channel."),
    WAIT_FOR_ANSWER("wait_for_answer", "Pause processing while waiting for the call to be answered."),
    ;

    private final String application;

    AppEnum(String application, String describe) {
        this.application = application;
    }

    public static AppEnum instance(String application) {
        AppEnum[] enums = values();
        for (AppEnum app : enums) {
            if (app.getApplication().equals(application)) {
                return app;
            }
        }
        return null;
    }


}
