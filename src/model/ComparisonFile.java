package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.stream.Collector;

/**
 * Created by Donghwan on 5/12/2016.
 * 비교하는 파일에 대해서 저장하는 클래스
 */
public class ComparisonFile implements TextPropertyProvider{
    private File source;
    private StringProperty content;

    public ComparisonFile(File source, String content){
        this.content = new SimpleStringProperty();
        setSource(source);
        setContent(content);
    }

    public void setSource(File source) {
        this.source = source;
    }

    public void setContent(String content){
        this.content.setValue(content);
    }

    public File getSource() {
        return source;
    }
    @Override
    public StringProperty textProperty(){
        return content;
    }

    public String getContentToString() {
        return content.getValue();
    }
}
