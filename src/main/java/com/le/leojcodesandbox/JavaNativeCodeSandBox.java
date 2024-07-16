package com.le.leojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.le.leojcodesandbox.model.ExecuteCodeRequest;
import com.le.leojcodesandbox.model.ExecuteCodeResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandBox implements CodeSandBox {
    public static final String GLOBAL_CODE_DIR_NAME = "tmpCode";

    public static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";

    public static void main(String[] args) {
        JavaNativeCodeSandBox javaNativeCodeSandBox = new JavaNativeCodeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();

        String code = ResourceUtil.readStr("testCode.simpleComputeArgs/Main.java", StandardCharsets.UTF_8);

        executeCodeRequest.setInputList(Arrays.asList("1 2","3 4"));
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);

    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        //1. 把用户的代码保存为文件

        List<String> inputList = request.getInputList();
        String code = request.getCode();
        String language = request.getLanguage();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 将用户代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        //2. 编译代码，得到 class 文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile);
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            // 等待程序执行获取错误码
            int exitValue = compileProcess.waitFor();
            if (exitValue == 0 ) {
                System.out.println("编译成功");
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                // 逐行读取
                String compileOutputLine;
                StringBuilder compileOutputLineBuilder = new StringBuilder();
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputLineBuilder.append(compileOutputLine);
                }
                System.out.println(compileOutputLineBuilder);
            } else {
                //异常推出
                System.out.println("编译失败，错误码" + exitValue);
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                // 逐行读取
                String compileOutputLine;
                StringBuilder compileOutputLineBuilder = new StringBuilder();
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputLineBuilder.append(compileOutputLine);
                }

                // 分批获取进程的异常输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                // 逐行读取
                String compileLineError;
                StringBuilder compileLineErrorBuilder = new StringBuilder();
                while ((compileLineError = errorBufferedReader.readLine()) != null) {
                    compileLineErrorBuilder.append(compileLineError);
                }
                System.out.println(compileOutputLineBuilder);
                System.out.println(compileLineErrorBuilder);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        //3. 执行代码，得到输出结果
        //4. 收集整理输出结果
        //5. 文件清理，释放空间
        //6. 错误处理，提升程序健壮性

        return null;
    }
}
