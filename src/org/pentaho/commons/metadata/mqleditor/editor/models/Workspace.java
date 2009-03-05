package org.pentaho.commons.metadata.mqleditor.editor.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.pentaho.commons.metadata.mqleditor.IDomain;
import org.pentaho.commons.metadata.mqleditor.IModel;
import org.pentaho.commons.metadata.mqleditor.IQuery;
import org.pentaho.commons.metadata.mqleditor.beans.BusinessColumn;
import org.pentaho.commons.metadata.mqleditor.beans.Category;
import org.pentaho.commons.metadata.mqleditor.beans.Condition;
import org.pentaho.commons.metadata.mqleditor.beans.Order;
import org.pentaho.commons.metadata.mqleditor.beans.Query;
import org.pentaho.ui.xul.XulEventSourceAdapter;

public class Workspace extends XulEventSourceAdapter implements IQuery{

  private UIModel model;
  private UIDomain selectedDomain;
  private List<UIDomain> domains;
  private List<UICategory> categories;
  private UICategory selectedCategory;
  private UIOrder selectedOrder;
  
  private UIBusinessColumn selectedColumn;
  
  private Columns selectedColumns = new Columns();
  private Conditions conditions = new Conditions();
  private Orders orders = new Orders();
  private String queryStr;
  
  public Workspace(){
    setupListeners();
  }
  
  /*
   * Adopt all values of bean version of Query as UI-enabled "Workspace". 
   */
  public void wrap(Query thinWorkspace){
    
    for( BusinessColumn col : thinWorkspace.getColumns()){
      selectedColumns.add(UIBusinessColumn.wrap(col));
    }
    for( Order order : thinWorkspace.getOrders()){
      orders.add(UIOrder.wrap(order));
    }
    for( Condition condition : thinWorkspace.getConditions()){
      conditions.add(UICondition.wrap(condition));
    }
    
    setSelectedDomain(new UIDomain(thinWorkspace.getDomain()));
    setSelectedModel(UIModel.wrap(thinWorkspace.getModel()));
  }
  
  public void clear(){
    this.setOrders(new Orders());
    this.setSelectedColumns(new Columns());
    this.setConditions(new Conditions());
    setupListeners();
  }
  
  public void setupListeners(){
    selectedColumns.addPropertyChangeListener("children", new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt) {
        Workspace.this.firePropertyChange("selectedColumns", null, getSelectedColumns());
      }
    });
    conditions.addPropertyChangeListener("children", new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt) {
        Workspace.this.firePropertyChange("conditions", null, getConditions());
      }
    });
    orders.addPropertyChangeListener("children", new PropertyChangeListener(){
      public void propertyChange(PropertyChangeEvent evt) {
        Workspace.this.firePropertyChange("orders", null, getOrders());
      }
    });
  }
  
  public void setSelectedModel(UIModel m){
    UIModel prevVal = this.model;
    this.model = m;

    this.firePropertyChange("selectedModel", prevVal, this.model);
    this.firePropertyChange("categories", null, getCategories());
  }
  
  public UIModel getSelectedModel(){
    return model;
  }
  
  public UIBusinessColumn getColumnByPos(int pos){
    if(pos < 0 || getSelectedModel() == null){
      return null;
    }
    List<UICategory> children = getSelectedModel().getChildren();
    int curpos = 0;
    for(UICategory child : children){
      if(child.getChildren().size() + 1 +curpos > pos){
        //within this node
        int posInNode = pos - curpos - 1;
        if(posInNode < 0){
          //node was selected
          return null;
        }
        return child.getChildren().get(posInNode);
      } else {
        curpos += child.getChildren().size() + 1;
      }
    }
    return null;
  }
  
  
  public List<UICategory> getCategories() {
    
    return (this.model != null) ? this.model.getChildren() : null;
  }

  public void setCategories(List<UICategory> categories) {
    this.categories = categories;
    this.firePropertyChange("categories", null, getCategories());
  }

  public UICategory getSelectedCategory() {
  
    return selectedCategory;
  }

  public void setSelectedCategory(UICategory selectedCategory) {
  
    this.selectedCategory = selectedCategory;
    this.firePropertyChange("selectedCategory", null, this.selectedCategory);
    this.firePropertyChange("columns", null, getColumns());
    
  }

  public void setSelectedOrder(UIOrder selectedOrder) {
  
    this.selectedOrder = selectedOrder;
    this.firePropertyChange("selectedOrder", null, this.selectedOrder);
    this.firePropertyChange("orders", null, getOrders());
    
  }
  
  public List<UIBusinessColumn> getColumns(){
    return (this.selectedCategory != null) ? this.selectedCategory.getChildren() : null;
  }

  public UIBusinessColumn getSelectedColumn() {
  
    return selectedColumn;
  }

  public void setSelectedColumn(UIBusinessColumn selectedColumn) {
  
    this.selectedColumn = selectedColumn;
    this.firePropertyChange("selectedColumn", null, getSelectedColumn());
  }
  
  public void addColumn(UIBusinessColumn col){
    if(selectedColumns.contains(col)){
      return;
    }
    selectedColumns.add(col);
    
  }

  public void addCondition(UIBusinessColumn col){
    
    UICondition condition = new UICondition();
    condition.setColumn(col);
    conditions.add(condition);
    
  }

  public void addOrder(UIBusinessColumn col){
    
    UIOrder order = new UIOrder();
    order.setColumn(col);
    orders.add(order);
    
  }
  
  
  public Columns getSelectedColumns() {
  
    return selectedColumns;
  }

  public void setSelectedColumns(Columns columns) {
    this.selectedColumns = columns;
    this.firePropertyChange("selectedColumns", null, getSelectedColumns());
  }

  public void setSelectedColumns(List<UIBusinessColumn> selectedColumns) {
    this.selectedColumns = new Columns(selectedColumns);
    this.firePropertyChange("selectedColumns", null, getSelectedColumns());
  }

  public Conditions getConditions() {
    return conditions;
  }

  public void setConditions(Conditions conditions) {
    this.conditions = conditions;
    this.firePropertyChange("conditions", null, getConditions());
  }

  public Orders getOrders() {
    return orders;
  }

  public void setOrders(Orders orders) {
    this.orders = orders;
    this.firePropertyChange("orders", null, getOrders());
  }

  public IModel getModel() {
    return this.model;   
  }
  
  protected void setModel(UIModel model){
    this.model = model;
  }

  public UIDomain getSelectedDomain() {
    return selectedDomain;
  }

  public void setSelectedDomain(UIDomain selectedDomain){
    this.selectedDomain = selectedDomain;
    this.firePropertyChange("selectedDomain", null, selectedDomain);
  }

  public IDomain getDomain(){
    return this.selectedDomain;
  }
  
 
  public Query getQueryModel(){
    Query query = new Query();
    query.setCols(this.selectedColumns.getBeanCollection());
    query.setConditions(this.conditions.getBeanCollection());
    query.setMqlStr(this.getMqlStr());
    return query;
  }

  public String getMqlStr() {
    return queryStr;
  }
  
  public void setMqlStr(String query){
    this.queryStr = query;
  }

  public List<UIDomain> getDomains() {
    return domains;
  }

  public void setDomains(List<UIDomain> domains) {
    this.domains = domains;
    this.firePropertyChange("domains", null, domains); //$NON-NLS-1$
  }
  
  
  
}

  