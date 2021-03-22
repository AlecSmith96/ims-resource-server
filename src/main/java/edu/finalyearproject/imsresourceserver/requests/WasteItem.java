package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

@Data
public class WasteItem
{
    private int sku;
    private String reason;
    private int quantity;
    private String name = null;
}
