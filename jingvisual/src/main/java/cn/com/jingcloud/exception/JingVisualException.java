/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.jingcloud.exception;

/**
 *
 * @author liyong
 *
 * 自定义异常，用于json异常返回
 *
 */
public class JingVisualException extends Exception {

    public JingVisualException(String message) {
        super(message);
    }

}
