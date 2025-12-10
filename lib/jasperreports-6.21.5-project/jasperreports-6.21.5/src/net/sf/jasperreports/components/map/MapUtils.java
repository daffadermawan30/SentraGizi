/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.components.map;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.util.JacksonUtil;
import net.sf.jasperreports.web.util.VelocityUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class MapUtils {
    public static final String MAP_API_SCRIPT = "net/sf/jasperreports/components/map/resources/scripts/jasperreportsMapApi.js";
    public static final String EXTERNAL_SCRIPT_LOAD_API_SCRIPT = "net/sf/jasperreports/components/map/resources/scripts/externalScriptLoadApi.js";
    public static final String OVERLAPPING_MARKER_SPIDERFIER_SCRIPT = "net/sf/jasperreports/components/map/resources/scripts/oms_1.0.3.js";

    public static String getSimplifiedMapConfig(JasperReportsContext jasperReportsContext, JRGenericPrintElement element)
    {
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("mapCanvasId", "map_canvas_" + element.hashCode());
        MapUtils.prepareContextForVelocityTemplate(contextMap, jasperReportsContext, element);

        contextMap.put("googleMapsApiUrl", "");
        contextMap.put("markerClustererApiUrl", "");
        contextMap.put("jasperreportsMapApiScriptLocation", "");
        contextMap.put("overlappingMarkerSpiderfierApiScriptLocation", "");

        return VelocityUtil.processTemplate(MapElementJsonHandler.MAP_ELEMENT_JSON_TEMPLATE, contextMap);
    }

    public static void prepareContextForVelocityTemplate(
            Map<String, Object> velocityContext,
            JasperReportsContext jasperReportsContext,
            JRGenericPrintElement element) {

        JacksonUtil jacksonUtil = JacksonUtil.getInstance(jasperReportsContext);

        Float latitude = (Float) element.getParameterValue(MapComponent.ITEM_PROPERTY_latitude);
        latitude = latitude == null ? MapComponent.DEFAULT_LATITUDE : latitude;

        Float longitude = (Float) element.getParameterValue(MapComponent.ITEM_PROPERTY_longitude);
        longitude = longitude == null ? MapComponent.DEFAULT_LONGITUDE : longitude;

        Integer zoom = (Integer) element.getParameterValue(MapComponent.PARAMETER_ZOOM);
        zoom = zoom == null ? MapComponent.DEFAULT_ZOOM : zoom;

        String mapType = (String) element.getParameterValue(MapComponent.ATTRIBUTE_MAP_TYPE);
        mapType = (mapType == null ? MapComponent.DEFAULT_MAP_TYPE.getName() : mapType).toLowerCase();

        String mapId = (String) element.getParameterValue(MapComponent.PARAMETER_MAP_ID);
        mapId = (mapId == null ? MapComponent.DEMO_MAP_ID : mapId);

        Boolean markerClustering = (Boolean) element.getParameterValue(MapComponent.ATTRIBUTE_MARKER_CLUSTERING);
        markerClustering = markerClustering != null ? markerClustering.booleanValue() : false;

        Boolean markerSpidering = (Boolean) element.getParameterValue(MapComponent.ATTRIBUTE_MARKER_SPIDERING);
        markerSpidering = markerSpidering != null ? markerSpidering.booleanValue() : false;

        velocityContext.put("latitude", latitude);
        velocityContext.put("longitude", longitude);
        velocityContext.put("zoom", zoom);
        velocityContext.put("mapType", mapType);
        velocityContext.put("mapId", mapId);
        velocityContext.put("useMarkerClustering", markerClustering);
        velocityContext.put("useMarkerSpidering", markerSpidering);

        Map<String, Object> legendPropertiesParam = (Map<String,Object>) element.getParameterValue(MapComponent.PARAMETER_LEGEND_PROPERTIES);
        String legendProperties = legendPropertiesParam == null ? "{}" : jacksonUtil.getIndentedJsonString(legendPropertiesParam);
        velocityContext.put("legendProperties", legendProperties);

        Map<String, Object> resetMapPropertiesParam = (Map<String,Object>) element.getParameterValue(MapComponent.PARAMETER_RESET_MAP_PROPERTIES);
        String resetMapProperties = resetMapPropertiesParam == null ? "{}" : jacksonUtil.getIndentedJsonString(resetMapPropertiesParam);
        velocityContext.put("resetMapProperties", resetMapProperties);

        Map<String, Object> markerSeries = (Map<String,Object>) element.getParameterValue(MapComponent.PARAMETER_MARKERS);
        String markers = markerSeries == null || markerSeries.isEmpty() ? "{}" : jacksonUtil.getIndentedJsonString(markerSeries);
        velocityContext.put("markerList", markers);

        List<Map<String,Object>> pathList = (List<Map<String,Object>>) element.getParameterValue(MapComponent.PARAMETER_PATHS);
        String paths = pathList == null || pathList.isEmpty() ? "[]" : jacksonUtil.getIndentedJsonString(pathList);
        velocityContext.put("pathsList", paths);

        String defaultMarkerIcon = (String)element.getParameterValue(MapComponent.PARAMETER_DEFAULT_MARKER_ICON);
        velocityContext.put(MapComponent.PARAMETER_DEFAULT_MARKER_ICON, defaultMarkerIcon != null ? defaultMarkerIcon: "");
    }

    public static void addExternalURLs(Map<String, Object> velocityContext, JRGenericPrintElement element) {
        String reqParams = (String)element.getParameterValue(MapComponent.PARAMETER_REQ_PARAMS);
        String googleMapsApiUrl = "https://maps.googleapis.com/maps/api/js"; //FIXME: make this URL configurable via props
        if (reqParams != null) {
            googleMapsApiUrl += "?" + reqParams;
        }
        velocityContext.put("googleMapsApiUrl", googleMapsApiUrl);
        velocityContext.put("markerClustererApiUrl",
                "https://unpkg.com/@googlemaps/markerclusterer/dist/index.min.js"); //FIXME: make this URL configurable via props
    }
}
