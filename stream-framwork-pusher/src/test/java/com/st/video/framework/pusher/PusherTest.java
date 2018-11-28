package com.st.video.framework.pusher;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVBitStreamFilter;
import org.bytedeco.javacpp.avcodec.AVPacket;

import static org.bytedeco.javacpp.avcodec.*;
import static org.bytedeco.javacpp.avformat.*;
import static org.bytedeco.javacpp.avutil.*;

public class PusherTest {

    public static class TestThread implements Runnable{
        String inUrl = "rtsp://172.16.10.245:554/1136";
        String outUrl = "rtsp://localhost:554/play";

        public TestThread(String inUrl, String outUrl) {
            this.inUrl = inUrl;
            this.outUrl = outUrl;
        }

        public void run() {


            int videoindex = -1;
            int audioindex = -1;

            AVFormatContext ictx = new AVFormatContext(null);

            AVOutputFormat ofmt = null;

            //初始化网络库
            avformat_network_init();
            av_log_set_level(AV_LOG_ERROR);
            int ret = -1;
            AVDictionary inOptions = new AVDictionary();
            //ret = av_dict_set(inOptions,"rtsp_transport", "tcp", -1);
            //ret = av_dict_set(inOptions,"stream_loop", "-1", -1);
            //打开文件，解封文件头
            ret = avformat_open_input(ictx, inUrl, null, inOptions);
            if (ret < 0) {
                throw new RuntimeException("Could not open input file");
            }

            ret = avformat_find_stream_info(ictx, (PointerPointer) null);
            if (ret < 0) {
                throw new RuntimeException("Failed to retrieve input stream information");
            }

            av_dump_format(ictx, 0, inUrl, 0);


            //////////////////////////////////////////////////////////////////
            //                   输出流处理部分
            /////////////////////////////////////////////////////////////////
            AVFormatContext octx = new AVFormatContext(null);
            //如果是输入文件 flv可以不传，可以从文件中判断。如果是流则必须传
            //创建输出上下文
            ret = avformat_alloc_output_context2(octx, null, "rtsp", outUrl);
            if (ret < 0) {
                throw new RuntimeException("Failed avformat_alloc_output_context2");
            }
            ofmt = octx.oformat();
            //ret = av_opt_set(ofmt,"rtsp_transport","tcp",0);
            for (int i = 0; i < ictx.nb_streams(); i++) {
                //获取输入视频流
                AVStream in_stream = ictx.streams(i);
                //为输出上下文添加音视频流（初始化一个音视频流容器）
                AVStream out_stream = avformat_new_stream(octx, in_stream.codec().codec());
                if (out_stream == null) {
                    throw new RuntimeException("未能成功添加音视频流!");
                }

                //将输入编解码器上下文信息 copy 给输出编解码器上下文
                ret = avcodec_parameters_copy(out_stream.codecpar(), in_stream.codecpar());
                if (ret < 0) {
                    throw new RuntimeException("copy 编解码器上下文失败!");
                }
                out_stream.codecpar().codec_tag(0);
                out_stream.codec().codec_tag(0);
                if ((octx.oformat().flags() & AVFMT_GLOBALHEADER) != 1) {
                    out_stream.codec().flags(out_stream.codec().flags() | 0);
                }
            }

            //输入流数据的数量循环
            for (int i = 0; i < ictx.nb_streams(); i++) {
                if (ictx.streams(i).codec().codec_type() == AVMEDIA_TYPE_VIDEO) {
                    videoindex = i;
                    break;
                }
            }

            for (int i = 0; i < ictx.nb_streams(); i++)
            {
                if (ictx.streams(i).codec().codec_type() == AVMEDIA_TYPE_AUDIO) {
                    audioindex = i;
                    break;
                }
            }

            av_dump_format(octx, 0, outUrl, 1);


            //////////////////////////////////////////////////////////////////
            //                   准备推流
            /////////////////////////////////////////////////////////////////
            if ((ofmt.flags() & AVFMT_NOFILE) == 0) {

                ret = avio_open(octx.pb(), outUrl, AVIO_FLAG_WRITE);
                if (ret < 0) {
                    throw new RuntimeException("Could not open outfile!");
                }
            }
//        //写入头部信息
            ret = avformat_write_header(octx, (PointerPointer<Pointer>)null);
            if (ret < 0) {
                throw new RuntimeException("avformat_write_header失败!");
            }

            AVPacket pkt = new AVPacket();
            //获取当前的时间戳  微妙
            long start_time = av_gettime();
            long frame_index = 0;
            AVBitStreamFilter filter = av_bsf_get_by_name("aac_adtstoasc");
            while(true){
                AVStream in_stream,out_stream;
                //获取解码前数据//读包
                ret = av_read_frame(ictx, pkt);

                if (ret < 0) {
                    break;
                }
//                if (pkt.stream_index() == videoindex) {
//
//                    AVRational time_base = ictx.streams(videoindex).time_base();
//                    AVRational time_base_q = new AVRational();
//                    //计算视频播放时间//微妙
//                    long pts_time = av_rescale_q(pkt.dts(), time_base, time_base_q);
//                    //计算实际视频的播放时间
//                    long now_time = av_gettime() - start_time;
//
//                    AVRational avr = ictx.streams(videoindex).time_base();
//
//                    if (pts_time > now_time) {
//                        //睡眠一段时间（目的是让当前视频记录的播放时间与实际时间同步）
//                        try {
//                            Thread.sleep(pts_time - now_time);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                in_stream = ictx.streams(pkt.stream_index());
//                out_stream = octx.streams(pkt.stream_index());
                ret = av_interleaved_write_frame(octx, pkt);


                if (ret < 0) {
                    //throw new RuntimeException("发送数据包出错["+ret+"]!");
                }
                av_packet_unref(pkt);
            }
        }
    }
    public static void main(String[] args) {
        try {
            String inUrl = "rtsp://172.16.10.245:554/1136";
            String outUrl = "rtsp://localhost:554/play";
            new Thread(new TestThread(inUrl,outUrl)).start();

             inUrl = "rtsp://172.16.10.245:554/1137";
             outUrl = "rtsp://localhost:554/play1";
            new Thread(new TestThread(inUrl,outUrl)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
