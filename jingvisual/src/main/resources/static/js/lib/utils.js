function convertBytes(bytes) {
    if (bytes < 1024 * 1024) {
        return (bytes / 1024).toFixed(2) + " KB";
    } else if (bytes < 1024 * 1024 * 1024) {
        return (bytes / 1024 / 1024).toFixed(2) + " MB";
    } else if (bytes < 1024 * 1024 * 1024 * 1024) {
        return (bytes / 1024 / 1024 / 1024).toFixed(2) + " GB";
    } else {
        return (bytes / 1024 / 1024 / 1024 / 1024).toFixed(2) + " TB";
    }
}
function convertToMb(bytes) {

    return (bytes / 1024 / 1024).toFixed(2) + " MB";

}
function toLocalDate(UtcDate) {
    var localDate = "";
    if (UtcDate != null && UtcDate.length > 0) {
        var disconnected = new Date();
        disconnected.setISO8601(UtcDate);
        localDate = disconnected.pattern("yyyy-MM-dd hh:mm:ss");
    }
    return localDate;
}