package com.yupi.leoj.judge.codesandbox;

import com.yupi.leoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.leoj.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest request);
}
