package com.sentragizi.shared.models;

public class MenuComponent {
    private int id;
    private int menuId;
    private String componentName;   
    
    
    private String aiLabel;         
    private boolean isOptional;     
    

    private boolean needsRawCheck;  
    private String rawMaterialName; 

    
    public MenuComponent(int id, int menuId, String componentName, String aiLabel, boolean isOptional, boolean needsRawCheck, String rawMaterialName) {
        this.id = id;
        this.menuId = menuId;
        this.componentName = componentName;
        this.aiLabel = aiLabel;        
        this.isOptional = isOptional;  
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    
    public MenuComponent(String componentName, String aiLabel, boolean isOptional, boolean needsRawCheck, String rawMaterialName) {
        this.componentName = componentName;
        this.aiLabel = aiLabel;
        this.isOptional = isOptional;
        this.needsRawCheck = needsRawCheck;
        this.rawMaterialName = rawMaterialName;
    }

    
    public int getId() { return id; }
    public int getMenuId() { return menuId; }
    public String getComponentName() { return componentName; }

    
    public String getAiLabel() { 
        
        return aiLabel == null ? "" : aiLabel; 
    }
    
    public boolean isOptional() { return isOptional; }

    public boolean isNeedsRawCheck() { return needsRawCheck; }
    public String getRawMaterialName() { return rawMaterialName; }
}