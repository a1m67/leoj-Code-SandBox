package com.le.leojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.le.leojcodesandbox.model.ExecuteCodeRequest;
import com.le.leojcodesandbox.model.ExecuteCodeResponse;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandBoxImpl implements CodeSandBox {
    public static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static void main(String[] args) {
        JavaNativeCodeSandBoxImpl
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        String code = ResourceUtil.readStr("testCode.simpleComputeArgs", StandardCharsets.UTF_8);

        executeCodeRequest.setInputList(Arrays.asList("1 2","3 4"));
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse =


    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        List<String> inputList = request.getInputList();
        String code = request.getCode();
        String language = request.getLanguage();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        //判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        //将用户代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        return null;
    }
}
