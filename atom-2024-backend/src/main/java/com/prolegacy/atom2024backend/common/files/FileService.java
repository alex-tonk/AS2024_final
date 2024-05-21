package com.prolegacy.atom2024backend.common.files;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Optional;

@Service
public class FileService {

    public byte[] readFile(String path,
                           String ip,
                           String dir,
                           String userName,
                           String password,
                           String domain) throws IOException {
        try (SMBClient client = new SMBClient()) {
            try (Connection connection = client.connect(ip)) {
                AuthenticationContext ac = new AuthenticationContext(
                        userName,
                        Optional.ofNullable(password).map(String::toCharArray).orElse(new char[0]),
                        domain
                );
                Session session = connection.authenticate(ac);
                try (DiskShare share = (DiskShare) session.connectShare(dir)) {
                    File file = share.openFile(
                            path,
                            EnumSet.of(AccessMask.GENERIC_READ),
                            null,
                            EnumSet.of(SMB2ShareAccess.FILE_SHARE_READ),
                            SMB2CreateDisposition.FILE_OPEN,
                            null
                    );
                    try (InputStream inputStream = file.getInputStream()) {
                        return inputStream.readAllBytes();
                    }
                }
            }
        }
    }

    public boolean fileExists(String path,
                              String ip,
                              String dir,
                              String userName,
                              String password,
                              String domain) throws IOException {
        try (SMBClient client = new SMBClient()) {
            try (Connection connection = client.connect(ip)) {
                AuthenticationContext ac = new AuthenticationContext(
                        userName,
                        Optional.ofNullable(password).map(String::toCharArray).orElse(new char[0]),
                        domain
                );
                Session session = connection.authenticate(ac);
                try (DiskShare share = (DiskShare) session.connectShare(dir)) {
                    return share.fileExists(path);
                }
            }
        }
    }

    public void saveFile(String path,
                         String ip,
                         String dir,
                         String userName,
                         String password,
                         String domain,
                         byte[] content) throws IOException {
        try (SMBClient client = new SMBClient()) {
            try (Connection connection = client.connect(ip)) {
                var ac = new AuthenticationContext(userName, password.toCharArray(), domain);
                Session session = connection.authenticate(ac);
                try (DiskShare share = (DiskShare) session.connectShare(dir)) {
                    var file = share.openFile(path,
                            EnumSet.of(AccessMask.GENERIC_WRITE),
                            EnumSet.of(FileAttributes.FILE_ATTRIBUTE_NORMAL),
                            EnumSet.of(SMB2ShareAccess.FILE_SHARE_WRITE),
                            SMB2CreateDisposition.FILE_OVERWRITE_IF,
                            EnumSet.of(SMB2CreateOptions.FILE_RANDOM_ACCESS)
                    );
                    try (OutputStream outputStream = file.getOutputStream()) {
                        outputStream.write(content);
                        outputStream.flush();
                    }
                }
            }
        }
    }
}
