from fastapi import Depends, FastAPI
from routers import authenticate, users,gpt_streaming_api

from dotenv import load_dotenv
load_dotenv()

app = FastAPI()

app.include_router(authenticate.router)
app.include_router(users.router)
app.include_router(gpt_streaming_api.router)

@app.get("/")
async def root():
    return {"message": "Hello World"}
