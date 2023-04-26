package com.automate.df.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Api {
    public int apiId;
    public Object apiHeaders;
    public String apiMethod;
    public Object apiRequestBody;
    public String apiUrl;
}
