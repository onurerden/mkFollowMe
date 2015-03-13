package com.belbim.kopter.followme;

/**
 * Created by asay on 24.12.2014.
 */
public class Kopter
{
    private String isActive;

    private String uid;

    private String id;

    private String latestTouch;

    private Status status;

    private String name;

    public String getIsActive ()
    {
        return isActive;
    }

    public void setIsActive (String isActive)
    {
        this.isActive = isActive;
    }

    public String getUid ()
    {
        return uid;
    }

    public void setUid (String uid)
    {
        this.uid = uid;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getLatestTouch ()
    {
        return latestTouch;
    }

    public void setLatestTouch (String latestTouch)
    {
        this.latestTouch = latestTouch;
    }

    public Status getStatus ()
    {
        return status;
    }

    public void setStatus (Status status)
    {
        this.status = status;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }
}