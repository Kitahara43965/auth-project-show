package com.example.myauthapp.auth.validation.group;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({
    RequiredGroup.class,
    FormatGroup.class,
    BusinessGroup.class,
    CompareGroup.class,
    Default.class
})
public interface ValidationSequence {
}