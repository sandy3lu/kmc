package com.yunjing.eurekaclient2.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface DefaultProcess {

    String OUTPUT = "my_output";
    String INPUT = "my_input";

    @Input(DefaultProcess.INPUT)
    SubscribableChannel input();

    @Output(DefaultProcess.OUTPUT)
    MessageChannel output();
}
