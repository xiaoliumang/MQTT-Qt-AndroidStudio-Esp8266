#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QtWidgets/QMessageBox>
#include <windows.h>
MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    m_client = new QMqttClient(this);
    m_client->setHostname("ip地址");
    m_client->setPort(1883);
    m_client->setUsername("admin203");
    m_client->setPassword("admin203");
    m_client->setWillTopic("mtopic/2");
    m_client->setClientId("qt");
    m_client->connectToHost();
    // 设置 MQTT 客户端的回调函数
    connect(m_client, &QMqttClient::stateChanged, this, &MainWindow::updateLogStateChange);
    connect(m_client, &QMqttClient::disconnected, this, &MainWindow::brokerDisconnected);

    connect(m_client, &QMqttClient::messageReceived, this, [this](const QByteArray &message, const QMqttTopicName &topic) {
        const QString content = QDateTime::currentDateTime().toString()
                    + QLatin1String(" Received Topic: ")
                    + topic.name()
                    + QLatin1String(" Message: ")
                    + message
                    + QLatin1Char('\n');
        if(message.toStdString()=="on")
        {
           // ui->textEdit->insertPlainText("");
            keybd_event(0x26,0,0,0);
        }
        if(message=="down")
        {
            keybd_event(0x28,0,0,0);
        }
        if(message=="left")
        {
            keybd_event(0x25,0,0,0);
        }
        if(message=="right")
        {
            keybd_event(0x27,0,0,0);
        }

        ui->textEdit->insertPlainText(content);
    });

    connect(m_client, &QMqttClient::pingResponseReceived, this, [this]() {
        const QString content = QDateTime::currentDateTime().toString()
                                + QLatin1String(" PingResponse")
                                + QLatin1Char('\n');
        ui->textEdit->insertPlainText(content);
    });
    connect(ui->pushButton,&QPushButton::clicked,this,[=]()
            {keybd_event(0x26,0,0,0);keybd_event(0x1,0,0,0);
        auto subscription = m_client->subscribe(QString("mtopic/2"),
                                                static_cast<quint8>(QString("mtopic/2").toUInt()));
        if (!subscription) {
            QMessageBox::critical(this, QLatin1String("Error"), QLatin1String("Could not subscribe. Is there a valid connection?"));
                    return;
        }
            });
    updateLogStateChange();
}
MainWindow::~MainWindow()
{
    delete ui;
}
void MainWindow::updateLogStateChange()
{
    const QString content = QDateTime::currentDateTime().toString()
                            + QLatin1String(": State Change")
                            + QString::number(m_client->state())
                            + QLatin1Char('\n');
    ui->textEdit->insertPlainText(content);
}
void MainWindow::brokerDisconnected()
{
    // ui->lineEditHost->setEnabled(true);
    // ui->spinBoxPort->setEnabled(true);
    // ui->buttonConnect->setText(tr("Connect"));
}
void MainWindow::on_buttonSubscribe_clicked()
{

    // auto subscription = m_client->subscribe();
    // if (!subscription) {
    //     QMessageBox::critical(this, QLatin1String("Error"), QLatin1String("Could not subscribe. Is there a valid connection?"));
    //     return;
    // }
}

