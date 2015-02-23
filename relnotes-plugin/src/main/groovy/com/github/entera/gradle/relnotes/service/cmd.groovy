package com.github.entera.gradle.relnotes.service

import java.lang.ProcessBuilder.Redirect

class CommandRunner {

    static String arg(Object arg) {
        return arg instanceof HiddenArgument ? arg.arg : arg.toString()
    }

    static HiddenArgument hiddenArg(String arg) {
        return new HiddenArgument(arg: arg)
    }

    File directory = null
    boolean logging = true

    Process run(Object... args) {
        if (logging) {
            print "run: \"${args.join(" ")}\"... "
        }
        def process = runImpl(args.collect { arg(it) })
        if (logging) {
            println "done."
        }
        return process
    }

    private Process runImpl(List<String> args) {
        def processBuilder = new ProcessBuilder()
            .command(args)
            .redirectOutput(Redirect.PIPE)
        if (directory) {
            processBuilder.directory(directory)
        }
        def process = processBuilder.start()
        //process.waitFor()
        return process
    }

    private static class HiddenArgument {
        String arg = null

        String toString() {
            return "<hidden>"
        }
    }

}
