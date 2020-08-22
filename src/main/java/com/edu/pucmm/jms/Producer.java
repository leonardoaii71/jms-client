package com.edu.pucmm.jms;
import com.edu.pucmm.jms.EndPointMessage;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.util.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;


@Configuration
@EnableScheduling
@Component
public class Producer {

    @Autowired
    JmsTemplate jmsTemplate;

    @Value("${sensorID:1}") // value after ':' is the default
    Integer sensorID;

    public String enviarMensaje(String cola, String msg) {

        jmsTemplate.convertAndSend(cola, msg);
        System.out.println("sent msg"+msg);
        return "Published Successfully";
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 3000)
    public void enviarMensajesAuto() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalDateTime date = LocalDateTime.now();

        try {
            String result = mapper.writeValueAsString(date);

            EndPointMessage emessage = new EndPointMessage(date.toString(), sensorID, new Random().nextFloat() * (40f - 18f) + 18f, new Random().nextFloat() * 100f);
            System.out.println(emessage.getIdDispositivo() + emessage.getFechaGeneracion());
            jmsTemplate.convertAndSend("notificacion_sensores", emessage);
            System.out.println(emessage);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}