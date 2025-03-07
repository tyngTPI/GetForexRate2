package com.example.getforexrate2.repository;

import com.example.getforexrate2.model.ForexRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForexRateRepository extends MongoRepository<ForexRate, String> {
    // 以日期查詢資料
    Optional<ForexRate> findByDate(Date date);

    // 以日期collect查詢資料
    @Query(value = "{ 'date' : { $in: ?0 } }", fields = "{ 'date' : 1, '_id' : 0 }")
    List<ForexRate> findDatesByDateIn(List<Date> dates);

    // 查詢日期區間的資料
    List<ForexRate> findByDateBetween(Date startDate, Date endDate);

    @Query("{ 'date' : { $gte: ?0, $lte: ?1 } }")
    List<ForexRate> findByDateRange(Date startDate, Date endDate);
}
