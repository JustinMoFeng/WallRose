from fastapi import Depends, FastAPI
from routers import users,gpt_streaming_api,login
from dependencies.path_dependencies import get_query_token, verify_token

app = FastAPI()

app.include_router(login.router)
app.include_router(users.router,dependencies=[Depends(verify_token)])
app.include_router(gpt_streaming_api.router,dependencies=[Depends(verify_token)])

@app.get("/")
async def root():
    return {"message": "Hello World"}
