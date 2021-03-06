package controller.mainWindow.editorPane;

import controller.mainWindow.editorPane.listView.CompResultsViewControllerInterface;
import controller.CompModeDisableReceiver;
import controller.mainWindow.editorPane.textArea.EditorTextAreaControllerInterface;
import model.editorModel.EditorModelInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import view.ConfirmationAlertFactory;
import view.ErrorAlertFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Donghwan on 5/15/2016.
 *
 * 편집 창의 액션을 관리하는 컨트롤러
 */
public class EditorPaneFXController implements Initializable, EditorPaneControllerInterface {
    // 뷰 상테에 따라서 EditorPaneFXController가 해야하는 행동이 다르다.
    private interface ViewMode {
        void handleLoadAction();
        void handleEditAction();
    }
    private class CompMode implements ViewMode{
        @Override
        public void handleLoadAction() {
            if(compModeDisableReceiver != null) compModeDisableReceiver.disableCompareMode(); // 비교 모드에서 해제되는 것이므로 이를 알려야 함.
            clearSelection();
            loadFromFile();
        }

        @Override
        public void handleEditAction() {
            // 비교 모드를 끝내고 편집 모드로 바꿈
            clearSelection();
            switchEditorTextArea();
            setEditable(true);
            if(compModeDisableReceiver != null) compModeDisableReceiver.disableCompareMode(); // 비교 모드에서 해제되는 것이므로 이를 알려야 함.
        }
    }
    private class EditMode implements ViewMode{
        @Override
        public void handleLoadAction() {
            loadFromFile();
        }

        @Override
        public void handleEditAction() {
            // 편집 모드에서 읽기 전용과 쓰기 가능 모드 사이에서 전환한다.
            boolean editable = editorTextAreaFXController.isEditable();
            editorTextAreaFXController.setEditable(!editable);
            editButton.setSelected(!editable);
        }
    }

    private EditorModelInterface model;
    private ViewMode viewMode;
    private EditorTextAreaControllerInterface editorTextAreaFXController;
    private CompResultsViewControllerInterface compResultListViewFXController;
    private CompModeDisableReceiver compModeDisableReceiver;

    @FXML
    private Pane rootPane;
    @FXML
    private Pane contentPane;
    @FXML
    private Label filePathLabel;
    @FXML
    private ToggleButton editButton;
    @FXML
    private Button saveButton;

    @FXML
    private void handleLoadAction(ActionEvent event){
        viewMode.handleLoadAction();
    }

    @FXML
    private void handleEditAction(ActionEvent event){
        viewMode.handleEditAction();
    }

    @FXML
    private void handleSaveAction(ActionEvent event){
        saveToFile();
    }

    @Override
    public void clearSelection() {
        compResultListViewFXController.clearSelection();
    }

    @Override
    public void setModel(EditorModelInterface model){
        this.model = model;
        model.fileReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null)  filePathLabel.setText(newValue.getPath());
            else filePathLabel.setText("No file");
        });
        editorTextAreaFXController.setModel(this.model.getObservableTextModel());
        compResultListViewFXController.setModel(this.model.getObservableListModel());
    }

    @Override
    public int getSelectedIndex() {
        return compResultListViewFXController.getSelectedIndex();
    }

    @Override
    public void scrollTo(int index){
        compResultListViewFXController.scrollTo(index);
    }

    @Override
    public void switchCompResultsView(){
        // 비교 결과를 보여주는 뷰로 변경
        viewMode = new CompMode();
        setEditable(false);
        setContentNode(compResultListViewFXController.getContentNode());
    }

    public void setCompModeDisableReceiver(CompModeDisableReceiver compModeDisableReceiver){
        this.compModeDisableReceiver = compModeDisableReceiver;
    }

    @Override
    public Node getContentNode() {
        return rootPane;
    }

    private void switchEditorTextArea(){
        // 텍스트 에이리어로 교체한다.
        viewMode = new EditMode();
        setContentNode(editorTextAreaFXController.getContentNode());
    }

    private void setDisableEditModeButtons(boolean disable){
        // 편집 모드와 관련된 버튼을 비활성화 한다.
        editButton.setDisable(false);
        saveButton.setDisable(false);
    }

    private void saveToFile(){
        try {
            model.save();
        }catch(IOException ioe){
            Alert fileSaveErrorAlert = ErrorAlertFactory.newFileSaveErrorAlert();
            fileSaveErrorAlert.show();
        }
    }

    private void loadFromFile(){
        if(model.isFileLoaded()){
            // 이미 다른 파일을 편집중이면 저장할 지 물어봐야 함.
            Alert saveEditedFileAlert = ConfirmationAlertFactory.newSaveEditedFileConfirmationAlert();
            saveEditedFileAlert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> saveToFile());
        }
        File selectedFile = showFileChooser();
        if(selectedFile == null) return;
        try {
            model.load(selectedFile);
            setEditable(false);
            setDisableEditModeButtons(false);
            switchEditorTextArea();
        }catch (UncheckedIOException uioe) {
            if (uioe.getCause() instanceof MalformedInputException) {
                Alert fileEncodingErrorAlert = ErrorAlertFactory.newFileEncodingErrorAlert();
                fileEncodingErrorAlert.show();
            }
        }catch  (MalformedInputException mie){
            Alert fileEncodingErrorAlert = ErrorAlertFactory.newFileEncodingErrorAlert();
            fileEncodingErrorAlert.show();
        }catch (IOException ioe){
            Alert fileLoadErrorAlert = ErrorAlertFactory.newFileLoadErrorAlert();
            fileLoadErrorAlert.show();
        }
    }

    private void setEditable(boolean editable){
        // 편집 버튼과 텍스트 에이리어의 편집 상태를 바꾼다.
        editButton.setSelected(editable);
        editorTextAreaFXController.setEditable(editable);
    }

    private File showFileChooser(){
        // FileChooser로 불러올 파일 선택
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt"));
        return fileChooser.showOpenDialog(rootPane.getScene().getWindow());
    }

    private void setContentNode(Node node) {
        // 파일 내용을 보여주는 노드 교체
        // 반드시 내용을 보여주는 AnchorPane은 하나의 자식만 가져야 한다.
        List<Node> children = contentPane.getChildren();
        if(children.size() == 0) children.add(0, node);
        else children.set(0, node);
    }

    private void addContentNodeProperty(Node node){
        // 내용을 보여주는 노드에 앵커페인 속성을 추가해서 특정 창에 고정해놓는다.
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // 텍스트 에이리어 로드
            FXMLLoader editorTextAreaFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/EditorTextArea.fxml"));
            editorTextAreaFXMLLoader.load();
            editorTextAreaFXController = editorTextAreaFXMLLoader.getController();
            // 비교 리스트창 로드
            FXMLLoader compResultsViewFXMLLoader = new FXMLLoader(getClass().getResource("/fxml/CompResultsView.fxml"));
            compResultsViewFXMLLoader.load();
            compResultListViewFXController = compResultsViewFXMLLoader.getController();
            // 내용을 출력하는 노드를 편집 창의 앵커페인 속성을 붙여서 특정 위치에만 표시되도록 고정한다.
            addContentNodeProperty(editorTextAreaFXController.getContentNode());
            addContentNodeProperty(compResultListViewFXController.getContentNode());
            // 로드가 끝나면 텍스트 에이리어로 교체한다.
            switchEditorTextArea();
        }catch(IOException ioe){
            Alert viewLoadAlert = ErrorAlertFactory.newViewLoadErrorAlert();
            viewLoadAlert.show();
        }
    }
}
