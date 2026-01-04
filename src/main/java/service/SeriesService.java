package service;


import Modules.Series;
import persistence.ISeriesRepository;


public class SeriesService implements ISeriesService {
    private static SeriesService instance = null;
    private final ISeriesRepository seriesRepository;

    public SeriesService(ISeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public static SeriesService getInstance(ISeriesRepository seriesRepository){
        if (instance == null) {
            instance = new SeriesService(seriesRepository);
        }
        return instance;
    }
    @Override
    public Series addSeries(Series series, long userId) {

        return seriesRepository.addSeries(series, userId);
    }

    @Override
    public boolean deleteSeries( int mediaId, long userId) {
        if (mediaId <= 0) throw new IllegalArgumentException("mediaId is missing");
        return seriesRepository.deleteSeries(mediaId, userId);
    }
}
