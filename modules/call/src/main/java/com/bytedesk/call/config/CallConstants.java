package com.bytedesk.call.config;

public class CallConstants {

	private CallConstants() {
	}

	public static final String USER_CONTEXT_DEFAULT = "default";

	public static final String DIRECTORY_DOMAIN_DEFAULT = "default";

	public static final String DIRECTORY_NAME = "directory";

	public static final String MEDIA_MODE_AUDIO = "audio";

	public static final String DEFAULT_TARGET = "1000";

	public static final String DEFAULT_FREESWITCH_PASSWORD = "12345679";

	public static final String LOOPBACK_IPV4 = "127.0.0.1";

	public static final String LOCALHOST = "localhost";

	public static final String BIND_ALL_IPV4 = "0.0.0.0";

	public static final String SOFIA_PROFILE_EXTERNAL = "external";

	public static final String SOFIA_PROFILE_INTERNAL = "internal";

	public static final String DIALPLAN_CONTEXT_PUBLIC = "public";

	public static final String SOFIA_ACTION_RESCAN = "rescan";

	public static final String ESL_COMMAND_RELOADXML = "reloadxml";

	public static final String ESL_COMMAND_RELOADACL = "reloadacl";

	public static final String ESL_COMMAND_XML_FLUSH_CACHE = "xml_flush_cache";

	public static final String FREESWITCH_VAR_DEFAULT_PASSWORD = "$${default_password}";

	public static final String FREESWITCH_VAR_DEFAULT_PROVIDER = "$${default_provider}";

	public static final String FREESWITCH_VAR_DEFAULT_AREACODE = "$${default_areacode}";

	public static final String DEFAULT_DIRECTORY_DIAL_STRING = "{^^:sip_invite_domain=${dialed_domain}:presence_id=${dialed_user}@${dialed_domain}}${sofia_contact(*/${dialed_user}@${dialed_domain})},${verto_contact(${dialed_user}@${dialed_domain})}";

	public static final int DEFAULT_MAX_REGISTRATIONS_PER_EXTENSION = 5;

	public static final String DEFAULT_GROUP_NAME = "default";

	public static final String ENV_FREESWITCH_DEFAULT_PASSWORD = "FREESWITCH_DEFAULT_PASSWORD";

	public static final String ENV_FREESWITCH_EXTERNAL_SIP_IP = "FREESWITCH_EXTERNAL_SIP_IP";

	public static final String ENV_FREESWITCH_DOMAIN = "FREESWITCH_DOMAIN";

	public static final String ENV_FREESWITCH_OUTBOUND_CALLER_NAME = "FREESWITCH_OUTBOUND_CALLER_NAME";

	public static final String ENV_FREESWITCH_OUTBOUND_CALLER_ID = "FREESWITCH_OUTBOUND_CALLER_ID";

	public static final String DEFAULT_OUTBOUND_CALLER_NAME = "FreeSWITCH";

	public static final String DEFAULT_OUTBOUND_CALLER_ID = "0000000000";

	public static final String LEGACY_DEFAULT_HOLD_MEDIA_URL = "https://www.weiyuai.cn/tts-quering.mp3";

	public static final String LEGACY_LOCAL_STREAM_DEFAULT_HOLD_MEDIA_URL = "local_stream://default";

	public static final String LEGACY_LOCAL_STREAM_HOLD_MEDIA_URL = "local_stream://moh";

	public static final String LEGACY_LOCAL_STREAM_HOLD_MEDIA_8000_URL = "local_stream://moh/8000";

	public static final String LEGACY_TONE_STREAM_HOLD_MEDIA_URL = "tone_stream://%(1000,0,425,475)";

	public static final String DEFAULT_HOLD_MEDIA_URL = "local_stream://moh/8000";

	public static final String DEFAULT_CONSULT_EXTENSION_NUMBERS = "1006,1007,1008";

	public static final String DEFAULT_TRANSFER_TARGET_NUMBERS = "1006,1007,1008";

	public static final String DEFAULT_CONFERENCE_TARGET_NUMBERS = "1006,1007,1008";

	public static final String DEFAULT_IVR_TARGET_NUMBERS = "5004,5005";

	public static final String ENV_HTTAPI_MRCP_PROBE = "HTTAPI_MRCP_PROBE";

	public static final String ENV_HTTAPI_MRCP_HOST = "HTTAPI_MRCP_HOST";

	public static final String ENV_HTTAPI_MRCP_PORT = "HTTAPI_MRCP_PORT";

	public static final int DEFAULT_HTTAPI_MRCP_PORT = 8060;
}
