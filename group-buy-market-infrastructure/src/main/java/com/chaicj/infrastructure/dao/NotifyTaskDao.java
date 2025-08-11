package com.chaicj.infrastructure.dao;

import com.chaicj.infrastructure.dao.po.NotifyTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotifyTaskDao {

    void insert(NotifyTask notifyTask);

    List<NotifyTask> queryGroupBuySuccessNotifyList();

    void updateNotifyTaskStatus(Long id);
}
