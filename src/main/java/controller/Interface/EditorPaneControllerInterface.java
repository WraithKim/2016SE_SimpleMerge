package controller.Interface;

import model.editorModel.EditorModelInterface;

/**
 * Created by Donghwan on 5/26/2016.
 *
 * 편집 창의 컨트롤러 인터페이스
 */
public interface EditorPaneControllerInterface extends ContentNodeProvider, SingleSelectionListViewControllerInterface{
    /**
     * 해당 컨트롤러가 참조할 모델을 설정한다.
     * @param model 컨트롤러가 참조할 모델
     */
    void setModel(EditorModelInterface model);

    /**
     * 비교 결과를 보여주는 뷰로 편집 창을 전환한다.
     */
    void switchCompResultsView();

    /**
     * 비교 모드를 끄는 명령을 받는 객체를 등록한다.
     * @param compareModeDisabler 비교 모드를 끄는 명령을 받는 객체
     */
    void setCompareModeDisabler(CompareModeDisabler compareModeDisabler);
}
