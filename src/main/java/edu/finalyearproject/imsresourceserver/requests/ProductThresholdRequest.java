package edu.finalyearproject.imsresourceserver.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductThresholdRequest
{
    private int newThreshold;
}
