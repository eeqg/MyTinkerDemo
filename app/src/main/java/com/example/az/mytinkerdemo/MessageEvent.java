package com.example.az.mytinkerdemo;

public class MessageEvent {
        public int eventType;
        public Object data;

        public MessageEvent() {
        }

        public MessageEvent(int eventType) {
            this(eventType, (Object)null);
        }

        public MessageEvent(int eventType, Object obj) {
            this.eventType = eventType;
            this.data = obj;
        }
    }