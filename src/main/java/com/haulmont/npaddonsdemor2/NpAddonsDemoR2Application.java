package com.haulmont.npaddonsdemor2;

import com.haulmont.npaddonsdemor2.entity.Owner;
import com.haulmont.npaddonsdemor2.entity.Pet;
import com.haulmont.npaddonsdemor2.repository.OwnerRepository;
import com.haulmont.npaddonsdemor2.repository.PetRepository;
import com.haulmont.npaddonsdemor2.service.OwnerServiceInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class NpAddonsDemoR2Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(NpAddonsDemoR2Application.class, args);
    }

    @Override
    public void run(String... args) {
        //some code here
    }
}
