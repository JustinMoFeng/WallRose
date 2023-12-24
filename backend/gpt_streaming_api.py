import openai
from openai import AsyncOpenAI
import os
import asyncio
from dotenv import load_dotenv

async def gpt_api_stream(prompt):
    """为提供的对话消息创建新的回答 (流式传输)
    """
    try:
        load_dotenv()
        client = AsyncOpenAI(
            api_key = os.getenv("OPENAI_API_KEY"),
            base_url = os.getenv("BASE_URL")
        )
        stream = await client.chat.completions.create(
            model="gpt-4",
            messages=[
                {
                    "role": "user",
                    "content": prompt,
                }
            ],
            stream=True,
        )
        async for chunk in stream:
            print(chunk.choices[0].delta.content or "", end="")
    except Exception as err:
        return f"OpenAI API 异常: {err}"

if __name__ == "__main__":
    prompt = "There are 9 birds in the tree, the hunter shoots one, how many birds are left in the tree？"
    asyncio.run(gpt_api_stream(prompt))
