from fastapi import Header, HTTPException, Request
    


async def verify_token(request: Request, wr_token: str = Header(None)):
    request_path = request.url.path
    if "register" in request_path or "login" in request_path:
        return    
    elif not wr_token:
        raise HTTPException(status_code=400, detail="No necessary token provided")
    else:
        return



async def get_query_token(token: str):
    if token != "jessica":
        raise HTTPException(status_code=400, detail="No Jessica token provided")