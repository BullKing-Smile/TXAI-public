package com.txai.servicemap.service;

import com.txai.common.dto.ResponseResult;
import com.txai.common.response.TrackResponse;
import com.txai.servicemap.remote.TrackClient;
import org.springframework.stereotype.Service;

@Service
public class TrackService {

    private final TrackClient trackClient;

    public TrackService(TrackClient trackClient) {
        this.trackClient = trackClient;
    }

    public ResponseResult<TrackResponse> add(String tid) {
        return trackClient.add(tid);
    }
}
