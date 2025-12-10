define("jasperreportsMapApi", function() {
    return async function initMap() {
        const { Map, InfoWindow } = await google.maps.importLibrary("maps");
        const { AdvancedMarkerElement, PinElement } = await google.maps.importLibrary("marker");

        const publicApi = {};
        let infowindow;
        const replacements = {
            latitude: "lat",
            longitude: "lng",
            icon: "glyph"
        };

        const defaultGlyphColor = "#000000";

        const internalApi = {
            configurePinElementFromIcon: function (parentKey, parentProps, parentOptions) {
                let width, height,
                    //originX, originY, anchorX, anchorY,
                    pk = parentKey.indexOf(".url") < 0 ? parentKey : parentKey.substring(0, parentKey.indexOf(".url") + 1);

                width = parentProps[pk + '.width'] ? parseInt(parentProps[pk + '.width']) : null;
                height = parentProps[pk + '.height'] ? parseInt(parentProps[pk + '.height']) : null;

                // originX = parentProps[pk + '.origin.x'] ? parseInt(parentProps[pk + '.origin.x']) : 0;
                // originY = parentProps[pk + '.origin.y'] ? parseInt(parentProps[pk + '.origin.y']) : 0;
                //
                // anchorX = parentProps[pk + '.anchor.x'] ? parseInt(parentProps[pk + '.anchor.x']) : 0;
                // anchorY = parentProps[pk + '.anchor.y'] ? parseInt(parentProps[pk + '.anchor.y']) : 0;

                parentOptions.content = document.createElement("img");
                parentOptions.content.src = parentProps[parentKey];
                if(width) {
                    parentOptions.content.width = width;
                }
                if(height) {
                    parentOptions.content.height = height;
                }
            },
            configurePinElementFromColor: function (parentKey, parentProps, parentOptions) {
                const pinBg = "#"+ parentProps[parentKey];
                const pinElem = new PinElement({
                    background: pinBg,
                    scale: this.getPinScale(parentProps["size"]),
                });
                if (parentProps.label && parentProps.label.length > 0) {
                    pinElem.glyphColor = defaultGlyphColor;
                    pinElem.glyph = parentProps.label;
                }
                parentOptions.content = pinElem.element;
            },
            configurePinElementFromLabel: function (parentKey, parentProps, parentOptions) {
                parentOptions.content = new PinElement({
                    glyph: parentProps[parentKey],
                    glyphColor: defaultGlyphColor,
                    scale: this.getPinScale(parentProps["size"]),
                }).element;
            },
            configureDefaultPinElement: function (parentOptions) {
                parentOptions.content = new PinElement().element;
                return parentOptions;
            },
            createInfo: function (parentProps) {
                var pp = parentProps;
                if (pp['infowindow.content'] && pp['infowindow.content'].length > 0) {
                    var po = {
                        content: pp['infowindow.content']
                    };
                    if (pp['infowindow.pixelOffset']) {
                        po['pixelOffset'] = pp['infowindow.pixelOffset'];
                    }
                    if (pp['infowindow.latitude'] && pp['infowindow.longitude']) {
                        po['position'] = {"lat": pp['infowindow.latitude'], "lng": pp['infowindow.longitude']};
                    }
                    if (pp['infowindow.maxWidth']) {
                        po['maxWidth'] = pp['infowindow.maxWidth'];
                    }
                    return new InfoWindow(po);
                }
                return null;
            },
            placeMarkers: function (seriesConfig, map, isForExport, useMarkerSpidering) {
                let markers = seriesConfig.markers, markerArr = [];
                if (markers) {
                    var j;
                    for (var i = 0; i < markers.length; i++) {
                        var markerProps = markers[i];
                        var markerLatLng = {"lat": markerProps['latitude'], "lng": markerProps['longitude']};
                        var markerOptions = {
                            position: markerLatLng
                        };

                        // for spidering, do not link marker to map directly
                        if (!useMarkerSpidering) {
                            markerOptions.map = map;
                        }

                        if (markerProps['icon.url'] && markerProps['icon.url'].length > 0) {
                            this.configurePinElementFromIcon('icon.url', markerProps, markerOptions);
                        } else if (markerProps['icon'] && markerProps['icon'].length > 0) {
                            this.configurePinElementFromIcon('icon', markerProps, markerOptions);
                        } else if (markerProps['color'] && markerProps['color'].length > 0) {
                            this.configurePinElementFromColor('color', markerProps, markerOptions);
                        } else if (markerProps['label'] && markerProps['label'].length > 0) {
                            this.configurePinElementFromLabel('label', markerProps, markerOptions);
                        } else {
                            this.configureDefaultPinElement(markerProps, markerOptions);
                        }
                        if (markerProps['shadow.url'] && markerProps['shadow.url'].length > 0) this.configurePinElementFromIcon('shadow.url', markerProps, markerOptions);
                        else if (markerProps['shadow'] && markerProps['shadow'].length > 0) markerOptions['shadow'] = markerProps['shadow'];
                        if(i === 0 && markerOptions.content) {
                            if(markerOptions.content.src) {
                                seriesConfig.firstMarkerIcon = markerOptions.content.outerHTML;
                            } else if (markerOptions.content.children && markerOptions.content.children[0]) {
                                seriesConfig.firstMarkerIcon = markerOptions.content.children[0].outerHTML;
                            }
                        }
                        let reservedProps = {};
                        for (j in markerProps) {
                            if (markerProps.hasOwnProperty(j) && !markerOptions.hasOwnProperty(j)) {
                                if(j === "color" || j === "label" || j === "url" || j === "target") {
                                    reservedProps[j] = markerProps[j];
                                }
                                else if(j !== "latitude" && j !== "longitude" && j !== "size" && j.indexOf(".") < 0) {
                                    markerOptions[j] = markerProps[j];
                                }
                           }
                        }
                        const marker = new AdvancedMarkerElement(markerOptions);
                        for (k in reservedProps) {
                            marker[k] = reservedProps[k];
                        }
                        // when in export mode, do not add unnecessary listener
                        if (!isForExport) {
                            marker['info'] = this.createInfo(markerProps);
                            var clickEvent = useMarkerSpidering ? 'spider_click' : 'click';
                            google.maps.event.addListener(marker, clickEvent, function () {
                                if (map.autocloseinfo && infowindow) infowindow.close();
                                if (this['info']) {
                                    infowindow = this['info'];
                                    this['info'].open(map, this);
                                } else if (this['url'] && this['url'].length > 0) {
                                    window.open(this['url'], this['target']);
                                }
                            });
                        }
                        markerArr.push(marker);
                    }
                    if (!seriesConfig.firstMarkerIcon) {
                        seriesConfig.firstMarkerIcon = "";
                    }
                }
                return markerArr;
            },
            drawPaths: function (p, map, isForExport) {
                if (p) {
                    for (var k = 0; k < p.length; k++) {
                        var props = p[k], o = {}, l = [], isPoly = false;
                        var poly;
                        for (prop in props) {
                            if (prop === 'locations' && props[prop]) {
                                var loc = props[prop];
                                for (var j = 0; j < loc.length; j++) {
                                    var latln = loc[j];
                                    l.push({"lat": latln['latitude'], "lng": latln['longitude']});
                                }
                            } else if (prop === 'isPolygon') {
                                isPoly = this.getBooleanValue(props[prop]);
                            } else if (prop === 'visible' || prop === 'editable' || prop === 'clickable' || prop === 'draggable' || prop === 'geodesic') {
                                o[prop] = this.getBooleanValue(props[prop]);
                            } else {
                                o[prop] = props[prop];
                            }
                        }
                        o['map'] = map;
                        if (isPoly) {
                            o['paths'] = l;
                            poly = new google.maps.Polygon(o);
                        } else {
                            o['path'] = l;
                            poly = new google.maps.Polyline(o);
                        }

                        // when in export mode, do not add unnecessary listener
                        if (!isForExport) {
                            if (o['path.hyperlink']) {
                                google.maps.event.addListener(poly, 'click', function () {
                                    window.open(this['path.hyperlink'], this['path.hyperlink.target'])
                                });
                            }
                        }
                    }
                }
            },
            placeSeriesMarkers: function (map, markerSeries, isForExport, globalUseMarkerSpidering) {
                var markerSeriesNames = this.getObjectKeys(markerSeries),
                    markerSeriesConfigBySeriesName = {}, i, ln, seriesName;

                for (i = 0, ln = markerSeriesNames.length; i < ln; i++) {
                    seriesName = markerSeriesNames[i];
                    var seriesConfig = markerSeries[seriesName];
                    var useMarkerSpidering = seriesConfig.markerSpidering != null ? seriesConfig.markerSpidering : globalUseMarkerSpidering;

                    var useMarkerClustering = null;
                    if (seriesConfig.markerClustering != null) {
                        if (seriesConfig.markerClustering === true || seriesConfig.markerClustering === "true") {
                            useMarkerClustering = true;
                        }
                        if (seriesConfig.markerClustering === false || seriesConfig.markerClustering === "false") {
                            useMarkerClustering = false;
                        }
                    }

                    markerSeriesConfigBySeriesName[seriesName] = {
                        useMarkerSpidering: useMarkerSpidering,
                        useMarkerClustering: useMarkerClustering,
                        legendIcon: seriesConfig.legendIcon,
                        googleMarkers: this.placeMarkers(seriesConfig, map, isForExport, useMarkerSpidering),
                        firstMarkerIcon: seriesConfig.firstMarkerIcon ? seriesConfig.firstMarkerIcon : ""
                    };
                }

                return markerSeriesConfigBySeriesName;
            },
            enableSpidering: function (map, markerSeriesConfigBySeriesName) {
                var markerSeriesNames = this.getObjectKeys(markerSeriesConfigBySeriesName),
                    i, j, seriesName, markerSeriesConfig, oms = null;
                for (i = 0; i < markerSeriesNames.length; i++) {
                    seriesName = markerSeriesNames[i];
                    markerSeriesConfig = markerSeriesConfigBySeriesName[seriesName];
                    if (markerSeriesConfig.useMarkerSpidering) {
                        if (oms === null) {
                            oms = new OverlappingMarkerSpiderfier(map, {
                                markersWontMove: true,
                                markersWontHide: true,
                                basicFormatEvents: true,
                                keepSpiderfied: true
                            });
                        }
                        for (j = 0; j < markerSeriesConfig.googleMarkers.length; j++) {
                            oms.addMarker(markerSeriesConfig.googleMarkers[j]);
                        }
                    }
                }

                return oms;
            },
            enableClustering: function (map, markerSeriesConfigBySeriesName, globalUseMarkerClustering) {
                var markerSeriesNames = this.getObjectKeys(markerSeriesConfigBySeriesName),
                    markerClustersBySeriesName = {},
                    globalClusterMarkers = [],
                    globalClusterSeries = [],
                    i, ln, seriesName, markerSeriesConfig;
                for (i = 0, ln = markerSeriesNames.length; i < ln; i++) {
                    seriesName = markerSeriesNames[i];
                    markerSeriesConfig = markerSeriesConfigBySeriesName[seriesName];

                    if (markerSeriesConfig.useMarkerClustering === null && globalUseMarkerClustering) {
                        this.extendArray(globalClusterMarkers, markerSeriesConfig.googleMarkers);
                        globalClusterSeries.push(seriesName);
                    } else if (markerSeriesConfig.useMarkerClustering) {
                        markerClustersBySeriesName[seriesName] = new markerClusterer.MarkerClusterer({
                            map: map,
                            markers: markerSeriesConfig.googleMarkers
                        });
                    }
                }

                if (globalClusterMarkers.length) {
                    var globalCluster = new markerClusterer.MarkerClusterer({
                        map: map,
                        markers: globalClusterMarkers
                    });
                    for (i = 0, ln = globalClusterSeries.length; i < ln; i++) {
                        seriesName = globalClusterSeries[i];
                        markerClustersBySeriesName[seriesName] = globalCluster;
                    }
                }

                return markerClustersBySeriesName;
            },
            drawLegend: function (legendProperties, map, mapCanvasId, markerSeriesConfigBySeriesName, markerClustersBySeriesName,
                                  overlappingMarkerSpiderfier, defaultMarkerIcon, isForExport) {
                if (this.getBooleanValue(legendProperties["enabled"])) {
                    var legendLabel = legendProperties["label"] || "Legend",
                        legendPosition = legendProperties["position"] || "RIGHT_CENTER",
                        legendOrientation = legendProperties["orientation"] || "vertical",
                        legendMaxWidth = legendProperties["legendMaxWidth"] || "100px",
                        legendMaxWidthFullscreen = legendProperties["legendMaxWidth.fullscreen"] || "150px",
                        legendMaxHeight = legendProperties["legendMaxHeight"] || "150px",
                        legendMaxHeightFullscreen = legendProperties["legendMaxHeight.fullscreen"] || "300px",
                        legendUseMarkerIcons = legendProperties["useMarkerIcons"] == null
                            ? true : this.getBooleanValue(legendProperties["useMarkerIcons"]);


                    var legendElement = document.getElementById(mapCanvasId + "_legend");
                    var titleContainer = document.createElement("div");
                    titleContainer.style.display = "flex";
                    titleContainer.style.alignItems = "center";

                    var titleElement = document.createElement("h3");
                    titleElement.insertAdjacentText("beforeend", legendLabel);

                    titleContainer.insertAdjacentElement("beforeend", titleElement);
                    legendElement.insertAdjacentElement("beforeend", titleContainer);

                    function showHideGoogleMarkers(markerArr, action) {
                        var actionMap = action === "show" ? map : null;
                        for (var i = 0; i < markerArr.length; i++) {
                            markerArr[i].setMap(actionMap);
                        }
                    }

                    var seriesToggleButton, i, ln, seriesName;
                    var markerSeriesNames = this.getObjectKeys(markerSeriesConfigBySeriesName);

                    var seriesMarkersWrapper = document.createElement("div");
                    seriesMarkersWrapper.style.display = "flex";

                    for (i = 0, ln = markerSeriesNames.length; i < ln; i++) {
                        seriesName = markerSeriesNames[i];
                        seriesToggleButton = document.createElement("button");
                        seriesToggleButton.textContent = seriesName;
                        seriesToggleButton.type = "button";
                        seriesToggleButton.style.backgroundColor = "#fff";
                        seriesToggleButton.style.border = "2px solid #fff";
                        seriesToggleButton.style.fontFamily = "Roboto,Arial,sans-serif";
                        seriesToggleButton.style.fontSize = "12px";
                        seriesToggleButton.style.verticalAlign = "top";
                        seriesToggleButton.style.cursor = "pointer";

                        !isForExport && (function (nameOfSeries) {
                            seriesToggleButton.addEventListener("click", function (event) {
                                var i, markerSeriesConfig = markerSeriesConfigBySeriesName[nameOfSeries];
                                if (markerSeriesConfig.action == null || markerSeriesConfig.action === "show") {
                                    markerSeriesConfig.action = "hide";
                                } else {
                                    markerSeriesConfig.action = "show";
                                }

                                // show/hide google markers
                                showHideGoogleMarkers(markerSeriesConfig.googleMarkers, markerSeriesConfig.action);

                                // if there is a cluster for the series, add/remove the markers from cluster
                                if (markerClustersBySeriesName[nameOfSeries]) {
                                    if (markerSeriesConfig.action === "hide") {
                                        markerClustersBySeriesName[nameOfSeries].removeMarkers(markerSeriesConfig.googleMarkers, false);
                                    } else {
                                        markerClustersBySeriesName[nameOfSeries].addMarkers(markerSeriesConfig.googleMarkers, false);
                                    }
                                }

                                // if spidering is enabled for the series, add/remove the markers from spiderfier
                                if (overlappingMarkerSpiderfier != null && markerSeriesConfig.useMarkerSpidering) {
                                    if (markerSeriesConfig.action === "hide") {
                                        for (i = 0; i < markerSeriesConfig.googleMarkers.length; i++) {
                                            overlappingMarkerSpiderfier.forgetMarker(markerSeriesConfig.googleMarkers[i]);
                                        }
                                    } else {
                                        for (i = 0; i < markerSeriesConfig.googleMarkers.length; i++) {
                                            overlappingMarkerSpiderfier.trackMarker(markerSeriesConfig.googleMarkers[i]);
                                        }
                                    }
                                }

                                if (markerSeriesConfig.action === "hide") {
                                    event.currentTarget.style.color = "#d8d8d8";
                                } else {
                                    event.currentTarget.style.color = "#000";
                                }
                            });
                        }(seriesName));

                        var divWrapper = document.createElement("div");
                        divWrapper.style.display = "flex";
                        divWrapper.style.alignItems = "center";

                        if (legendUseMarkerIcons) {
                            let markerSeriesConfig = markerSeriesConfigBySeriesName[seriesName],
                                legendMarkerIcon = markerSeriesConfig.legendIcon;
                            let markerImage;
                            if (!legendMarkerIcon) {
                                if(markerSeriesConfig.firstMarkerIcon && markerSeriesConfig.firstMarkerIcon.length >0) {
                                    markerImage = new DOMParser().parseFromString(markerSeriesConfig.firstMarkerIcon, "text/html").documentElement;
                                    this.addLegendSeriesIcon(markerImage,divWrapper);
                                } else if(defaultMarkerIcon && defaultMarkerIcon.length > 0) {
                                    legendMarkerIcon = defaultMarkerIcon;
                                } else {
                                    const imgElement = this.configureDefaultPinElement({});
                                    markerImage = new DOMParser().parseFromString(imgElement.content.children[0].outerHTML, "text/html").documentElement;
                                    this.addLegendSeriesIcon(markerImage,divWrapper);
                                }
                            }
                            if (legendMarkerIcon) {
                                markerImage = document.createElement("img");
                                markerImage.src = legendMarkerIcon;
                                this.addLegendSeriesIcon(markerImage,divWrapper);
                            }
                        }
                        //to create additional room for svg default markers, which are not resizable
                        var emptyDiv = document.createElement("div");
                        emptyDiv.style.width = "20px";
                        divWrapper.insertAdjacentElement("beforeend", emptyDiv);
                        divWrapper.insertAdjacentElement("beforeend", seriesToggleButton);

                        seriesMarkersWrapper.insertAdjacentElement("beforeend", divWrapper);
                    }

                    if (legendOrientation === "horizontal") {
                        titleContainer.style.marginRight = "20px";
                        legendElement.style.flexDirection = "row";
                        seriesMarkersWrapper.style.flexDirection = "row";
                        seriesMarkersWrapper.style.alignItems = "center";
                    } else {
                        titleContainer.style.marginRight = "0";
                        legendElement.style.flexDirection = "column";
                        seriesMarkersWrapper.style.flexDirection = "column";
                        seriesMarkersWrapper.style.alignItems = "flex-start";
                    }

                    if (legendPosition.indexOf("BOTTOM") !== -1) {
                        legendElement.style.marginBottom = "24px";
                    } else {
                        legendElement.style.marginBottom = "10px";
                    }

                    seriesMarkersWrapper.style.overflow = "auto";
                    legendElement.insertAdjacentElement("beforeend", seriesMarkersWrapper);

                    // apply max width/height to legend
                    legendElement.style.maxWidth = legendMaxWidth;
                    legendElement.style.maxHeight = legendMaxHeight;

                    !isForExport && google.maps.event.addListener(map, "bounds_changed", function () {
                        // detect fullscreen
                        if (map.getDiv().firstChild.clientHeight === window.innerHeight) { // fullscreen
                            legendElement.style.maxWidth = legendMaxWidthFullscreen;
                            legendElement.style.maxHeight = legendMaxHeightFullscreen;
                        } else { // not fullscreen
                            legendElement.style.maxWidth = legendMaxWidth;
                            legendElement.style.maxHeight = legendMaxHeight;
                        }
                    });

                    map.controls[google.maps.ControlPosition[legendPosition]].push(legendElement);
                }
            },
            drawResetMap: function (resetMapProperties, map, latitude, longitude, zoom) {
                if (this.getBooleanValue(resetMapProperties["enabled"])) {
                    var resetMapButton = document.createElement("button");
                    resetMapButton.textContent = resetMapProperties["label"] || "Reset map";
                    resetMapButton.type = "button";
                    resetMapButton.style.backgroundColor = "#fff";
                    resetMapButton.style.border = "2px solid #fff";
                    resetMapButton.style.fontFamily = "Roboto,Arial,sans-serif";
                    resetMapButton.style.fontSize = "12px";
                    resetMapButton.style.margin = "10px";
                    resetMapButton.style.borderRadius = "2px";
                    resetMapButton.style.cursor = "pointer";

                    resetMapButton.addEventListener("click", function () {
                        map.setCenter({lat: latitude, lng: longitude});
                        map.setZoom(zoom);
                    });

                    var controlPosition = resetMapProperties["position"] || "RIGHT_TOP";
                    map.controls[google.maps.ControlPosition[controlPosition]].push(resetMapButton);
                }
            },
            getBooleanValue: function (v) {
                return (v === true || v === 'true');
            },
            extendArray: function (destArr, sourceArr) {
                for (var i of sourceArr) {
                    destArr.push(i);
                }
            },
            getObjectKeys: function (object) {
                var props = [];
                for (var prop in object) {
                    if (object.hasOwnProperty(prop)) {
                        props.push(prop);
                    }
                }
                return props;
            },
            replaceKey: function (oldKey) {
                if (replacements[oldKey]) {
                    return replacements[oldKey];
                }
                return oldKey;
            },
            getPinScale: function(size) {
                return (size && size.length > 0
                    ? (size  === "tiny" ? 0.25 : (size === "small" ? 0.5 : (size === "mid" ? 0.75 : 1)))
                    : 1.0);
            },
            addLegendSeriesIcon: function(markerImage, divWrapper) {
                markerImage.style.width = "16px";
                markerImage.style.marginBottom = "5px";
                markerImage.style.zIndex = 2;
                divWrapper.insertAdjacentElement("beforeend", markerImage);
            }
        };

        publicApi.map = {
            draw: function ({mapCanvasId, mapInstanceData, isForExport}) {
                const {
                    zoom,
                    latitude,
                    longitude,
                    mapType,
                    mapId,
                    markerList,
                    useMarkerSpidering,
                    useMarkerClustering,
                    legendProperties,
                    defaultMarkerIcon,
                    resetMapProperties,
                    pathsList
                } = mapInstanceData;
                const googleMapsOptions = {
                    zoom: zoom,
                    center: {lat: latitude, lng: longitude},
                    mapId: mapId,
                    mapTypeId: mapType,
                    autocloseinfo: true
                };

                if (isForExport) {
                    Object.assign(googleMapsOptions, {
                        disableDefaultUI: true,
                        gestureHandling: 'none'
                    })
                }

                const map = new Map(document.getElementById(mapCanvasId), googleMapsOptions);

                /*
                    markerList is {} or
                    {
                        "marker_series_0": {
                        "markerClustering": true,
                            "markerSpidering": true,
                            "markers": []
                    },
                        "marker_series_1": {}
                    ...
                    }
                */
                const markerSeriesConfigBySeriesName = internalApi.placeSeriesMarkers(
                    map, markerList, false, useMarkerSpidering);

                // enable marker spidering only for the configured series
                let overlappingMarkerSpiderfier = null;
                if (!isForExport) {
                    overlappingMarkerSpiderfier = internalApi.enableSpidering(map, markerSeriesConfigBySeriesName);
                }

                // enable marker clustering globally and/or per series
                const markerClustersBySeriesName = internalApi.enableClustering(map, markerSeriesConfigBySeriesName, useMarkerClustering);

                // draw marker legend
                internalApi.drawLegend(legendProperties, map, mapCanvasId, markerSeriesConfigBySeriesName,
                    markerClustersBySeriesName, overlappingMarkerSpiderfier, defaultMarkerIcon, isForExport);

                // draw resetMap control
                if (!isForExport) {
                    internalApi.drawResetMap(resetMapProperties, map, latitude, longitude, zoom);
                }

                // draw paths
                internalApi.drawPaths(pathsList, map, isForExport);

                return Promise.resolve(map);
            }
        };

        return Promise.resolve(publicApi);
    };
});
