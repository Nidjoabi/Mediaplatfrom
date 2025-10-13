package handler;


import Modules.Series;
import service.ISeriesService;

public class SeriesHandler implements IMediaHandler<Series>{

    public ISeriesService seriesService;
    public SeriesHandler(ISeriesService seriesService) {
        this.seriesService = seriesService;
    }


    @Override
    public Series addMedia(Series mediaType){

        if(!validateMedia(mediaType))return null;
        return seriesService.addSeries(mediaType);
    }

    @Override
    public boolean validateMedia(Series media) {
        if (media == null) return false;

        return media.getTitle() != null && !media.getTitle().isBlank();
    }


}
