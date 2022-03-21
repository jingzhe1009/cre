package com.bonc.frame.service.modelCompare;

import com.bonc.frame.entity.modelCompare.entity.ModelCompareType;
import org.springframework.stereotype.Service;

@Service
public class PublicCompareImp extends AbstractModelCompare {
    @Override
    public boolean isSupport(String type) {
        return super.isSupport(type);
    }

    @Override
    public ModelCompareType getSupport() {
        return super.getSupport();
    }
}
