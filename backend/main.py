from fastapi import Depends, FastAPI
from routers import users,gpt_streaming_api,login

from dotenv import load_dotenv
load_dotenv()

app = FastAPI()

app.include_router(login.router)
app.include_router(users.router)
app.include_router(gpt_streaming_api.router)

@app.get("/")
async def root():
    return {"message": "Hello World"}
