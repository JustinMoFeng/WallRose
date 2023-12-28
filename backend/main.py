from fastapi import Depends, FastAPI
from routers import authenticate, users, gpt_streaming_api, chats

app = FastAPI()

app.include_router(authenticate.router)
app.include_router(users.router)
app.include_router(gpt_streaming_api.router)
app.include_router(chats.router)

@app.get("/")
async def root():
    return {"message": "Hello World"}
