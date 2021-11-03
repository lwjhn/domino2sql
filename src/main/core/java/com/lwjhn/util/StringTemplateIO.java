package com.lwjhn.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author: lwjhn
 * @Date: 2020-12-4
 * @Description: com.lwjhn.util
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class StringTemplateIO {
    protected Replicator replicator = null;

    public StringTemplateIO(Replicator replicator) throws Exception {
        if (replicator == null) throw new Exception("replicator is null . ");
        this.replicator = replicator;
    }

    public StringTemplateIO transform(BufferedReader input, BufferedWriter output) throws Exception {
        String line;
        while ((line = input.readLine()) != null) {
            output.write(StringTemplate.process(line, replicator).toString());
        }
        output.flush();
        return this;
    }

    public StringTemplateIO transform(InputStream input, OutputStream output, Charset charset) throws Exception {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter writer = null;
        try {
            inputStreamReader = new InputStreamReader(input, charset);
            reader = new BufferedReader(inputStreamReader);
            outputStreamWriter = new OutputStreamWriter(output, charset);
            writer = new BufferedWriter(outputStreamWriter);
            transform(reader, writer);
        } finally {
            AutoCloseableBase.close(reader, inputStreamReader);
            AutoCloseableBase.close(writer, outputStreamWriter);
        }
        return this;
    }

    public StringTemplateIO transform(BufferedReader input, OutputStream output, Charset charset) throws Exception {
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter writer = null;
        try {
            outputStreamWriter = new OutputStreamWriter(output, charset);
            writer = new BufferedWriter(outputStreamWriter);
            transform(input, writer);
            output.flush();
        } finally {
            AutoCloseableBase.close(writer, outputStreamWriter);
        }
        return this;
    }

    public StringTemplateIO transform(InputStream input, BufferedWriter output, Charset charset) throws Exception {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStreamReader = new InputStreamReader(input, charset);
            reader = new BufferedReader(inputStreamReader);
            transform(reader, output);
            output.flush();
        } finally {
            AutoCloseableBase.close(reader, inputStreamReader);
        }
        return this;
    }

    public StringTemplateIO transform(File input, File output, Charset charset) throws Exception {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            transform(fileInputStream = new FileInputStream(input),
                    fileOutputStream = new FileOutputStream(output),
                    charset);
        } finally {
            AutoCloseableBase.close(fileInputStream, fileOutputStream);
        }
        return this;
    }

    public StringTemplateIO transform(File input, File output) throws Exception {
        return transform(input, output, StandardCharsets.UTF_8);
    }

}
