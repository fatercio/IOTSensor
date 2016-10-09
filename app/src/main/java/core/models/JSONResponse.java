package core.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Diogo on 29/08/2016.
 */
public class JSONResponse implements Serializable {

    public channel channel;
    public feed[] feeds;
    public feed lastFeed;

}
