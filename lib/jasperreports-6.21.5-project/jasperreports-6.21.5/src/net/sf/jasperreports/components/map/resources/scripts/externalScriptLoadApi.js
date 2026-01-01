const __jr__scripts__cache__ = new Map();
const __jr__load__script__ = url => {
    if (__jr__scripts__cache__.has(url)) {
        return __jr__scripts__cache__.get(url);
    }
    const result = new Promise((resolve, reject) => {
        const script = document.createElement("script");
        const scriptEventHandler = (event) => {
            if (event.type === "load") {
                script.removeEventListener("load", scriptEventHandler);
                script.removeEventListener("error", scriptEventHandler);
                resolve();
            }
            if (event.type === "error") {
                script.removeEventListener("load", scriptEventHandler);
                script.removeEventListener("error", scriptEventHandler);
                reject(new Error("Unable to load script: " + url));
            }
        };
        script.src = url;
        script.async = true;
        script.addEventListener("load", scriptEventHandler);
        script.addEventListener("error", scriptEventHandler);
        document.head.appendChild(script);
    });
    __jr__scripts__cache__.set(url, result);
    return result;
};

const __jr__jsonp__load__script__ = (url, callbackParamName = "callback") => {
    return new Promise((resolve, reject) => {
        const callbackFunctionName = "__jr__callback__" + new Date().getTime().toString() + "__";
        window[callbackFunctionName] = resolve;

        const _url = new URL(url);
        _url.searchParams.append(callbackParamName, callbackFunctionName);

        __jr__load__script__(_url.href).catch(reject);
    });
}
