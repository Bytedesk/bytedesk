import json
import logging
import os
import zlib
import tempfile
import threading

from .ASRData import ASRDataSeg, ASRData


class BaseASR:
    SUPPORTED_SOUND_FORMAT = ["flac", "m4a", "mp3", "wav"]
    CACHE_FILE = os.path.join(tempfile.gettempdir(), "bk_asr", "asr_cache.json")
    _lock = threading.Lock()

    def __init__(self, audio_path: [str, bytes], use_cache: bool = False):
        self.audio_path = audio_path
        self.file_binary = None

        self.crc32_hex = None
        self.use_cache = use_cache

        self._set_data()

        self.cache = self._load_cache()

    def _load_cache(self):
        if not self.use_cache:
            return {}
        os.makedirs(os.path.dirname(self.CACHE_FILE), exist_ok=True)
        with self._lock:
            if os.path.exists(self.CACHE_FILE):
                try:
                    with open(self.CACHE_FILE, 'r', encoding='utf-8') as f:
                        cache = json.load(f)
                        if isinstance(cache, dict):
                            return cache
                except (json.JSONDecodeError, IOError):
                    return {}
            return {}

    def _save_cache(self):
        if not self.use_cache:
            return
        with self._lock:
            try:
                with open(self.CACHE_FILE, 'w', encoding='utf-8') as f:
                    json.dump(self.cache, f, ensure_ascii=False, indent=2)
                if os.path.exists(self.CACHE_FILE) and os.path.getsize(self.CACHE_FILE) > 10 * 1024 * 1024:
                    os.remove(self.CACHE_FILE)
            except IOError as e:
                logging.error(f"Failed to save cache: {e}")

    def _set_data(self):
        if isinstance(self.audio_path, bytes):
            self.file_binary = self.audio_path
        else:
            ext = self.audio_path.split(".")[-1].lower()
            assert ext in self.SUPPORTED_SOUND_FORMAT, f"Unsupported sound format: {ext}"
            assert os.path.exists(self.audio_path), f"File not found: {self.audio_path}"
            with open(self.audio_path, "rb") as f:
                self.file_binary = f.read()
        crc32_value = zlib.crc32(self.file_binary) & 0xFFFFFFFF
        self.crc32_hex = format(crc32_value, '08x')

    def _get_key(self):
        return f"{self.__class__.__name__}-{self.crc32_hex}"

    def run(self):
        k = self._get_key()
        if k in self.cache and self.use_cache:
            resp_data = self.cache[k]
        else:
            resp_data = self._run()
            # Cache the result
            self.cache[k] = resp_data
            self._save_cache()
        segments = self._make_segments(resp_data)
        return ASRData(segments)

    def _make_segments(self, resp_data: dict) -> list[ASRDataSeg]:
        raise NotImplementedError("_make_segments method must be implemented in subclass")

    def _run(self) -> dict:
        """ Run the ASR service and return the response data. """
        raise NotImplementedError("_run method must be implemented in subclass")



