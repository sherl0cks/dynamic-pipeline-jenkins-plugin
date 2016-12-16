package com.rhc.jenkinsfile.generator.cli;


public class CliAdapter {
    public static void main(String[] args) {
        JenkinsfileGeneratorCli cli = new JenkinsfileGeneratorCli();
        cli.process( args);
    }
}
