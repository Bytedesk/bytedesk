#
#
import logging
from fastapi import APIRouter, Request
# from sse_starlette import EventSourceResponse
import edge_tts

# https://github.com/rany2/edge-tts/blob/master/README.md
# https://tts.byylook.com/ai/text-to-speech?source=github
router = APIRouter(
    prefix='/tts',
    tags=['tts v1 apis']
)

#
# https://github.com/rany2/edge-tts/blob/master/examples/basic_generation.py
# # http://127.0.0.1:9007/api/v1/tts/test
# 列出音色：edge-tts --list-voices
@router.get("/test")
async def tts():
    TEXT = "Hello World!"
    VOICE = "en-GB-SoniaNeural"
    OUTPUT_FILE = "test.mp3"
    communicate = edge_tts.Communicate(TEXT, VOICE)
    await communicate.save(OUTPUT_FILE)
    return "ok"
