#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QMqttClient>
QT_BEGIN_NAMESPACE
namespace Ui {
class MainWindow;
}
QT_END_NAMESPACE

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    QMqttClient* m_client;
    MainWindow(QWidget *parent = nullptr);
    ~MainWindow();
    void updateLogStateChange();
    void brokerDisconnected();
    void on_buttonSubscribe_clicked();

private:
    Ui::MainWindow *ui;
};
#endif // MAINWINDOW_H
