#!/bin/env python
# coding=utf-8
#     File Name: algo.py
#     Author: Gu Shenlong
#     Mail: blackhero98@gmail.com
#     Created Time: Sat Nov 28 21:55:46 2015


import sys,os,math,time,logging,json
from gensim.models.word2vec import Word2Vec
import time
import tornado.ioloop
import tornado.web
import math
model = None

def normalize(vec):
    l = len(vec)
    weight = 0
    for item in vec:
        weight += item * item
    weight = math.sqrt(weight) 
    if weight == 0:
        return vec
    for i in range(0, l):
        vec[i] /= weight
    return vec

import json

class ResponseHandler(tornado.web.RequestHandler):
    def get(self):
        self.write("helloworld")
        return

class VectorHandler(tornado.web.RequestHandler):
    def get(self):
        global model
        input_text = self.get_argument("text")
        if input_text is None:
            self.write("fuck")
            return
        words = input_text.strip().split()
        res_vec = [0] * 300
        l = len(words)
        for i in range(0, l):
            try:
                res_vec = map(sum, zip(res_vec, model[words[i]]))
            except Exception as e:
                print e
        #binary-gram
        for i in range(0, l - 1):
            try:
                res_vec = map(sum, zip(res_vec, model["_".join((words[i], words[i + 1]))]))
            except:
                continue
        res_vec = normalize(res_vec)
        self.write(json.dumps(res_vec))

def make_app():
    return tornado.web.Application([
        (r"/vector", VectorHandler),
        (r"/resp", ResponseHandler),
        ])

def init():
    global model
    model = Word2Vec.load_word2vec_format('GoogleNews-vectors-negative300.bin', binary=True)

if __name__ == "__main__":
    init()
    print "init done"
    app = make_app()
    app.listen(8888)
    print "begin loop"
    tornado.ioloop.IOLoop.current().start()
