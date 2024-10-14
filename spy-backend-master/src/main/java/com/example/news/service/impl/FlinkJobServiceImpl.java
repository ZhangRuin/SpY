package com.example.news.service.impl;

import com.example.news.component.job.SaveJob;
import com.example.news.service.FlinkJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlinkJobServiceImpl implements FlinkJobService {

    @Autowired
    private SaveJob saveJob;

    public void runFlinkJob() {
        try {
            saveJob.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
