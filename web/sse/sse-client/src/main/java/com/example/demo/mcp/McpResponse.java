package com.example.demo.mcp;

public sealed interface McpResponse permits JsonMcpResponse, SseMcpResponse {}

