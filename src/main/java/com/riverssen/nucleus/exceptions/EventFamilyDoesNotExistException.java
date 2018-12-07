package com.riverssen.nucleus.exceptions;

public class EventFamilyDoesNotExistException extends Throwable
{
    public EventFamilyDoesNotExistException(String family)
    {
        super("event family '" + family + "' does not exist.");
    }
}
