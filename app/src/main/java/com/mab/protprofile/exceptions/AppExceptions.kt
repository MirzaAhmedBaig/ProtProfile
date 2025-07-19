package com.mab.protprofile.exceptions

// Custom exception: Thrown when the ID already exists in the collection
class IdAlreadyExistsException(
    id: String,
) : Exception("An item with '$id' already exists.")

// Custom exception: Thrown when the item is not found in the collection
class ItemNotFoundException(
    id: String,
) : Exception("Item with '$id' was not found.")
