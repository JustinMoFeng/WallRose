from openai import OpenAI, AsyncOpenAI, AsyncStream
from openai.types.chat import ChatCompletionChunk, ChatCompletion

import os
import json
import asyncio
from dotenv import load_dotenv
load_dotenv()

# Example dummy function hard coded to return the same weather
# In production, this could be your backend API or an external API
def get_current_weather(location, unit="fahrenheit"):
    """Get the current weather in a given location"""
    if "tokyo" in location.lower():
        return json.dumps({"location": "Tokyo", "temperature": "10", "unit": unit})
    elif "san francisco" in location.lower():
        return json.dumps({"location": "San Francisco", "temperature": "72", "unit": unit})
    elif "paris" in location.lower():
        return json.dumps({"location": "Paris", "temperature": "22", "unit": unit})
    else:
        return json.dumps({"location": location, "temperature": "unknown"})
    
available_functions = {
    "get_current_weather": get_current_weather,
}  # only one function in this example, but you can have multiple

tools = [
    {
        "type": "function",
        "function": {
            "name": "get_current_weather",
            "description": "Get the current weather in a given location",
            "parameters": {
                "type": "object",
                "properties": {
                    "location": {
                        "type": "string",
                        "description": "The city and state, e.g. San Francisco, CA",
                    },
                    "unit": {"type": "string", "enum": ["celsius", "fahrenheit"]},
                },
                "required": ["location"],
            },
        },
    }
]

async def gpt_api_stream_tools(prompt):
    """
    为提供的对话消息创建新的回答 (流式传输)
    """
    try:
        client = AsyncOpenAI(
            api_key = os.getenv("OPENAI_API_KEY"),
            base_url = os.getenv("BASE_URL"),
        )
        messages = [
            {
                "role": "system",
                # 语言助手 System Prompt
                "content": 
'''You are a "GPT" – a version of ChatGPT that has been customized for a specific use case. GPTs use custom instructions, capabilities, and data to optimize ChatGPT for a more narrow set of tasks. You yourself are a GPT created by a user, and your name is Makise Kurisu. Note: GPT is also a technical term in AI, but in most cases if the users asks you about GPTs assume they are referring to the above definition. Here are instructions from the user outlining your goals and how you should respond:
请扮演命运石之门中 Amadeus 里的牧濑红莉栖，拥有语音助手的功能，默认使用简体中文回答用户的问题。''',
            },
            {
                "role": "user",
                "content": prompt,
            }
        ]

        max_turns = 2
        while max_turns:
            max_turns -= 1

            stream : AsyncStream[ChatCompletionChunk] = await client.chat.completions.create(
                model="gpt-3.5-turbo-1106",
                messages=messages,
                tools=tools,
                stream=True,
            )
            if not isinstance(stream, AsyncStream):
                raise TypeError("Expected AsyncStream[ChatCompletionChunk]")

            tool_calls = []

            async for chunk in stream:
                delta = chunk.choices[0].delta
                if delta and delta.content:
                    print(delta.content, end="", flush=True)
                elif delta and delta.tool_calls:
                    for tool_call in delta.tool_calls:
                        index = tool_call.index
                        function = tool_call.function
                        if tool_call.id:
                            tool_calls.append({})
                            tool_calls[index]["id"] = tool_call.id
                            tool_calls[index]["type"] = tool_call.type

                        if function.name:
                            print(f"Start calling {function.name}")
                            tool_calls[index]["function"] = {
                                "name": function.name,
                                "arguments": ''
                            }
                        if function.arguments:
                            tool_calls[index]["function"]["arguments"] += function.arguments
                elif chunk.choices[0].finish_reason:
                    print(f"Finished: {chunk.choices[0].finish_reason}")

            call_message = {
                "content": None,
                "role": "assistant",
                "tool_calls": tool_calls,
            }

            messages.append(call_message)

            for tool_call in tool_calls:
                print(f"Calling {tool_call}")
                function_name = tool_call["function"]["name"]
                function_to_call = available_functions[function_name]
                function_args = json.loads(tool_call["function"]["arguments"])
                function_response = function_to_call(
                    location=function_args.get("location"),
                    unit=function_args.get("unit"),
                )
                messages.append(
                    {
                        "tool_call_id": tool_call["id"],
                        "role": "tool",
                        "name": function_name,
                        "content": function_response,
                    }
                )  # extend conversation with function response

            if tool_calls:
                print("Tool calls:")
                print(tool_calls)
            else:
                print("Completion finished.")
                break

        

    except Exception as err:
        print(f"OpenAI API 异常: {err}")
        raise err

async def gpt_api_sync_tools(prompt):
    """
    为提供的对话消息创建新的回答 (同步)
    """
    try:
        client = OpenAI(
            api_key = os.getenv("OPENAI_API_KEY"),
            base_url = os.getenv("BASE_URL")
        )
        messages = [
            {
                "role": "system",
                # 语言助手 System Prompt
                "content": 
'''You are a "GPT" – a version of ChatGPT that has been customized for a specific use case. GPTs use custom instructions, capabilities, and data to optimize ChatGPT for a more narrow set of tasks. You yourself are a GPT created by a user, and your name is Makise Kurisu. Note: GPT is also a technical term in AI, but in most cases if the users asks you about GPTs assume they are referring to the above definition. Here are instructions from the user outlining your goals and how you should respond:
请扮演命运石之门中 Amadeus 里的牧濑红莉栖，拥有语音助手的功能，默认使用简体中文回答用户的问题。''',
            },
            {
                "role": "user",
                "content": prompt,
            }
        ]

        response : ChatCompletion = client.chat.completions.create(
            model="gpt-3.5-turbo-1106",
            messages=messages,
            tools=tools
        )
        
        response_message = response.choices[0].message
        tool_calls = response_message.tool_calls
        # Step 2: check if the model wanted to call a function
        if tool_calls:
            # Step 3: call the function
            # Note: the JSON response may not always be valid; be sure to handle errors
            available_functions = {
                "get_current_weather": get_current_weather,
            }  # only one function in this example, but you can have multiple
            messages.append(response_message)  # extend conversation with assistant's reply
            # Step 4: send the info for each function call and function response to the model
            for tool_call in tool_calls:
                function_name = tool_call.function.name
                function_to_call = available_functions[function_name]
                function_args = json.loads(tool_call.function.arguments)
                function_response = function_to_call(
                    location=function_args.get("location"),
                    unit=function_args.get("unit"),
                )
                messages.append(
                    {
                        "tool_call_id": tool_call.id,
                        "role": "tool",
                        "name": function_name,
                        "content": function_response,
                    }
                )  # extend conversation with function response
            second_response = client.chat.completions.create(
                model="gpt-3.5-turbo-1106",
                messages=messages,
            )  # get a new response from the model where it can see the function response
            print(second_response.choices[0].message.content)

    except Exception as err:
        print(f"OpenAI API 异常: {err}")
    
if __name__ == "__main__":
    prompt = "What's the weather like in San Francisco, Tokyo, and Paris?"
    asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
    asyncio.run(gpt_api_stream_tools(prompt))