package com.kafkapublisher.test.service;

import com.kafkapublisher.test.vo.CreateProductRestModel;

public interface ProductService {

    String createProduct(CreateProductRestModel productRestModel);
}
