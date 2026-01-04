package service;


import Modules.Series;

public interface ISeriesService {
    Series addSeries(Series series, long userId);
    boolean deleteSeries(int mediaId, long userId);
}
