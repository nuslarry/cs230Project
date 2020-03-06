package com.example.demo.service;


import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

public class HDFSAccess {

    private static HDFSAccess single_instance=null;

    private FileSystem hdfs;

    private HDFSAccess() throws IOException {
        Configuration conf = new Configuration ();
        conf.set("fs.default.name", "hdfs://3.233.215.15:8020");
        hdfs = FileSystem.get(conf);
    }

    public static HDFSAccess getInstance() throws IOException {
        if (single_instance == null)
        {
            single_instance = new HDFSAccess();
        }

        single_instance.autoScale();
        return single_instance;
    }

    private void autoScale() throws IOException {
        FsStatus status = hdfs.getStatus();
        double fraction = (double)status.getRemaining()/status.getCapacity();
        if(fraction<0.1){
            //TODO: scale up

        }
    }

    public boolean createDirIfNotExist(String dirPath) throws IOException {
        hdfs.mkdirs(new Path(dirPath));
        return false;
    }

    public boolean createFile(String path) throws IOException {
        return hdfs.createNewFile(new Path(path));
    }

    public boolean uploadFile(InputStream src,String dstPath,int replicationFactor) throws IOException {
        hdfs.createNewFile(new Path(dstPath));
        FSDataOutputStream stream = hdfs.create(new Path(dstPath),(short)replicationFactor);
        IOUtils.copy(src,stream);
        src.close();
        stream.close();
        return false;
    }

    public InputStream readFile(String path) throws IOException {
        if(!hdfs.exists(new Path(path)))
            return null;
       return hdfs.open(new Path(path));
    }

    public boolean deleteFile(String path) throws IOException {
        return hdfs.exists(new Path(path)) && hdfs.delete(new Path(path),true);
    }

    public boolean exists(String path) throws IOException {
        return hdfs.exists(new Path(path));
    }

    public FSDataOutputStream appendFile(String path) throws IOException {
        if(!hdfs.exists(new Path(path)))
            return null;
        return hdfs.append(new Path(path));
    }

    public RemoteIterator<LocatedFileStatus> listFiles(String dir) throws IOException {
        return hdfs.listFiles(new Path(dir),true);
    }

    public FileStatus getFileStatus(String path) throws IOException {
        return hdfs.getFileStatus(new Path(path));
    }


}
